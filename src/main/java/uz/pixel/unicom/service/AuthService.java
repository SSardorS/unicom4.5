package uz.pixel.unicom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pixel.unicom.bot.payload.TelegramResultMessage;
import uz.pixel.unicom.bot.service.TelegramService;
import uz.pixel.unicom.enam.ResponseEnum;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.*;
import uz.pixel.unicom.payload.*;
import uz.pixel.unicom.repository.*;
import uz.pixel.unicom.security.JwtProvider;


import java.util.*;

@Service
public class AuthService extends AuthServiceAbs implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;


    @Autowired
    JwtProvider jwtProvider;


    @Autowired
    TelegramService telegramService;

    @Autowired
    TelegramUserRepository telegramUserRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    DepartmentRepository departmentRepository;



    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUsername(userName).orElseThrow(() -> new UsernameNotFoundException(userName));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseMessage registerUser(RegisterDto registerDto) {

        if (!registerDto.getPassword().equals(registerDto.getPrePassword()))
            return new ResponseMessage(false, ResponseEnum.PASSWORD_IS_NOT_SAME.getName(), ResponseEnum.PASSWORD_IS_NOT_SAME.getStatus());

        boolean superAdmin = userRepository.existsByUsername(registerDto.getUserName());
        if (superAdmin)
            return new ResponseMessage(false, ResponseEnum.USER_ALREADY_EXIST.getName(), ResponseEnum.USER_ALREADY_EXIST.getStatus());

        Optional<Role> userRole = roleRepository.findByName(RoleBasic.SUPER_ADMIN.name());
        if (userRole.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_IS_NOT_AVAILABLE.getName(), ResponseEnum.USER_IS_NOT_AVAILABLE.getStatus());

        Long chatId = 0l;

        try {
            chatId = Long.valueOf(registerDto.getTelegramUserid());
        } catch (Exception e) {
            chatId = 0l;
        }


        Optional<TelegramUser> terlegramUserOptional = telegramUserRepository.findByUserNameOrChatId(registerDto.getTelegramUserid(), chatId);
        if (terlegramUserOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.TELEGRAM_USER_NOT_FOUND.getName(), ResponseEnum.TELEGRAM_USER_NOT_FOUND.getStatus());

        int registerCode = (int) (Math.random() * 1000000);

        User user = new User(registerDto.getFullName(), registerDto.getUserName(), passwordEncoder.encode(registerDto.getPassword()), userRole.get(), false);


        user.setRegisterCode(registerCode);

//        User userSave = userRepository.save(user);
        TelegramUser telegramUser = terlegramUserOptional.get();
        telegramUser.setUser(user);

        userRepository.save(user);

        Long chatUser = telegramUser.getChatId();
        TelegramResultMessage telegramResultMessage = telegramService.verifyAccaunt(telegramUser);

        if (telegramResultMessage.isOk()) {
            return new ResponseMessage(true, "Succesul Register Plase verify accaunt because you can only use it after you have verified your account", 201);
        } else {
            return new ResponseMessage(false, "Verify code dont send",205);
        }


    }


    @Override
    public ResponseMessage loginUser(LoginDto loginDto) {
//         Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));
//         User user = (User) authenticate.getPrincipal();


//         if (!user.isEnabled())
//             return new ResponceMessage(false, "you must verify your account to use the site");

//         if (user == null)
//             return new ResponceMessage(false, "Login or password are wrong");


//         return new ResponceMessage(true, "Successful entered",jwtProvider.generateToken(user.getUsername()));

        Optional<User> byUsername = userRepository.findByUsername(loginDto.getUserName());

        if (byUsername.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        if (!byUsername.get().isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        boolean matches = passwordEncoder.matches(loginDto.getPassword(), byUsername.get().getPassword());

        if (!matches)
            return new ResponseMessage(false, ResponseEnum.LOGIN_PASSWORD_ARE_WRONG.getName(), ResponseEnum.LOGIN_PASSWORD_ARE_WRONG.getStatus());

        return new ResponseMessage(true, ResponseEnum.SUCCESSFULL_ENTERED.getName(), ResponseEnum.SUCCESSFULL_ENTERED.getStatus(), jwtProvider.generateToken(byUsername.get().getUsername()));

    }

    @Override
    public ResponseMessage verifyEmail(String username, int code) {

        Optional<User> byUsername = userRepository.findByUsername(username);

        if (byUsername.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        User user = byUsername.get();

        if (user.getRegisterCode() == code) {
            user.setEnabled(true);
            user.setRegisterCode(0);

            TelegramUser telegramUser = user.getTelegramUser();
            telegramUser.setActive(true);

            user.setTelegramUser(telegramUser);
//            TelegramUser telegramUserSaved = telegramUserRepository.save(telegramUser);

//            user.setTelegramUser(telegramUserSaved);
            userRepository.save(user);

            String text = "You are succesfull activated";

            telegramService.sendMessageVoid(telegramUser.getChatId(), text);

            return new ResponseMessage(true, ResponseEnum.ACCAUNT_ACTIVATED.getName(), ResponseEnum.ACCAUNT_ACTIVATED.getStatus());

        } else {
            return new ResponseMessage(false, ResponseEnum.WRONG_CODE.getName(), ResponseEnum.WRONG_CODE.getStatus());
        }
    }

    @Transactional(rollbackFor = MailSendException.class)
    @Override
    public ResponseMessage restartPasswordSending(String email) {
        Optional<User> byUsername = userRepository.findByUsername(email);

        if (byUsername.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        int code = (int) (Math.random() * 1000000);

        String message = "Your account password is restart! \n" + "Restart code";

        ResponseMessage responseMessage = telegramService.sendverifyCode(byUsername.get().getTelegramUser().getChatId(), code, message);

        if (responseMessage.isResponseBool()) {
            User user = byUsername.get();
            user.setRestartPasswordCode(code);
            userRepository.save(user);
            return responseMessage;
        } else {
            return responseMessage;
        }
    }


    @Override
    public ResponseMessage restartPasswordGetCode(RestartCode restartCode) {
        Optional<User> byRestartPasswordCode = userRepository.findByRestartPasswordCode(restartCode.getCode());

        if (byRestartPasswordCode.isPresent()) {
            if (restartCode.getPassword().equals(restartCode.getPrePassword())) {
                User user = byRestartPasswordCode.get();
                user.setPassword(passwordEncoder.encode(restartCode.getPassword()));
                user.setRestartPasswordCode(0);
                userRepository.save(user);
                return new ResponseMessage(true, ResponseEnum.SUCCESSULL_CHANGED.getName(), ResponseEnum.SUCCESSULL_CHANGED.getStatus());
            } else {
                return new ResponseMessage(false, ResponseEnum.PASSWORD_IS_NOT_SAME.getName(), ResponseEnum.PASSWORD_IS_NOT_SAME.getStatus());
            }
        } else {
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());
        }
    }

    @Override
    public ResponseMessage deleteAccauntSendingCode() {
        User userToken = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> byUsername = userRepository.findByUsername(userToken.getUsername());

        if (byUsername.isEmpty())
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        int code = (int) (Math.random() * 1000000);

        Long chatId = userToken.getTelegramUser().getChatId();

        String message = "Your account will delete! \n" + "Delte code";

        ResponseMessage responseMessage = telegramService.sendverifyCode(chatId, code, message);

        if (responseMessage.isResponseBool()) {
            User user = byUsername.get();
            user.setDeleteAccauntCode(code);
            userRepository.save(user);
            return responseMessage;
        } else {
            return responseMessage;
        }

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseMessage deleteAccaunt(DeleteAccauntInfo deleteAccauntInfo) throws Exception {

        User userToken = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<User> deleteAccauntCodeOptional = userRepository.findByDeleteAccauntCodeAndUsername(deleteAccauntInfo.getCode(), userToken.getUsername());


        if (deleteAccauntCodeOptional.isPresent()) {
            User userDeleted = deleteAccauntCodeOptional.get();

            boolean matches = passwordEncoder.matches(deleteAccauntInfo.getPassword(), userDeleted.getPassword());
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());

        } else {
            return new ResponseMessage(false, ResponseEnum.USER_NOT_FOUND.getName(), ResponseEnum.USER_NOT_FOUND.getStatus());
        }
    }

//    @Transactional(rollbackFor = MailSendException.class)
    @Override
    public ResponseMessage addmanager(ManagerDto managerDto) {

        User userIsSuperAdmin = User.getuser();

        if (!userIsSuperAdmin.isEnabled())
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        if (!userIsSuperAdmin.getRole().getName().equals(RoleBasic.SUPER_ADMIN.name()))
            return new ResponseMessage(false, ResponseEnum.NOT_AVIABLE.getName(), ResponseEnum.NOT_AVIABLE.getStatus());

        Optional<User> existsuser = userRepository.findByUsername(managerDto.getUsername());

        if (existsuser.isPresent()){
            User manager = existsuser.get();
            UUID departmentId = manager.getDepartment().getId();

            String managerRole = manager.getRole().getName();

            if (departmentId.equals(managerDto.getDepartment()) || managerRole.equals(RoleBasic.MANAGER.name()) || managerRole.equals(RoleBasic.USER.name()) || managerRole.equals(RoleBasic.DIRECTOR.name()) || managerRole.equals(RoleBasic.SUPER_ADMIN.name()))
                return new ResponseMessage(false, ResponseEnum.ALREADY_EXISTS.getName(), ResponseEnum.ALREADY_EXISTS.getStatus());
        }

        Optional<Role> role = roleRepository.findByName(managerDto.getRole());

        if (role.isEmpty())
            return new ResponseMessage(false, "Role is not found", 200);

//        Optional<Company> companyOptional = companyRepository.findByIdAndCreatedById(managerDto.getCompanyId(), userIsSuperAdmin.getId());
//
//        if (companyOptional.isEmpty())
//            return new ResponseMessage(false, "Company is not found", 200);

        Optional<Department> deparmentOptional = departmentRepository.findByIdAndCreatedById(managerDto.getDepartment(), userIsSuperAdmin.getId());

        if (deparmentOptional.isEmpty())
            return new ResponseMessage(false, "Department is not found", 200);

        Long telegramUserId = 0l;
        try {
            telegramUserId = Long.valueOf(managerDto.getTelegramuser());
        } catch (Exception e) {
            telegramUserId = 0l;
        }

        Optional<TelegramUser> telegramUserOptinal = telegramUserRepository.findByUserNameOrChatId(managerDto.getTelegramuser(), telegramUserId);

        if (telegramUserOptinal.isEmpty())
            return new ResponseMessage(false, "Telegram User not found", 200);

        TelegramUser telegramUser = telegramUserOptinal.get();


        Long userIdTelegram = Long.valueOf(managerDto.getTelegramuser());

        Optional<TelegramUser> telegramUserOptional = telegramUserRepository.findByUserNameOrChatId(telegramUser.getFullName(), userIdTelegram);

        if (telegramUserOptional.isEmpty())
            return new ResponseMessage(false, ResponseEnum.TELEGRAM_USER_NOT_FOUND.getName(), ResponseEnum.TELEGRAM_USER_NOT_FOUND.getStatus());


        TelegramResultMessage telegramResultMessage = telegramService.verifyAccaunt(telegramUserOptional.get());

        Department department = deparmentOptional.get();

        User user = new User();
        user.setFullName(managerDto.getFullName());
        user.setUsername(managerDto.getUsername());
        user.setPassword(passwordEncoder.encode(managerDto.getPassword()));
        user.setRole(role.get());
        user.setTelegramUser(telegramUser);
        user.setDepartment(department);
        user.setRegisterCode(telegramResultMessage.getResult().getMessageId());
        user.setCreatedBy(userIsSuperAdmin);
        telegramUser.setUser(user);
        department.setDepartmentManager(new ArrayList<>(Arrays.asList(
                user
        )));

        userRepository.save(user);


        if (telegramResultMessage.isOk()) {
            return new ResponseMessage(true, "Succesul Register Plase verify accaunt because you can only use it after you have verified your account", 201);
        } else {
            return new ResponseMessage(false, "Verify code dont send", 201);
        }
    }

    @Override
    public ResponseMessage findByIdOruserName(String idOrUsername, User createdby) {

        UUID uuid = UUID.fromString(idOrUsername);


        Optional<User> userOptional = userRepository.findByIdOrUsernameAndCreatedById(uuid, idOrUsername, createdby.getId());

        if (userOptional.isEmpty())
            return new ResponseMessage(false, "null", 200);

        User user = userOptional.get();

        TelegramUser telegramUser = user.getTelegramUser();

        if (telegramUser==null)
            return new ResponseMessage(false, ResponseEnum.TELEGRAM_USER_NOT_FOUND.getName(), ResponseEnum.TELEGRAM_USER_NOT_FOUND.getStatus());

        if (user.isEnabled()==false || telegramUser.isActive()==false)
            return new ResponseMessage(false, ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getName(), ResponseEnum.ACCAUNT_IS_NOT_VERIFIED.getStatus());

        return new ResponseMessage(false, "Find", 200,user);

    }


}
