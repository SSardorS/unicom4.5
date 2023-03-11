package uz.pixel.unicom.companent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pixel.unicom.enam.Permission;
import uz.pixel.unicom.enam.RoleBasic;
import uz.pixel.unicom.entity.*;
import uz.pixel.unicom.repository.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    TelegramUserRepository telegramUserRepository;



    @Value("${spring.sql.init.mode}")
    private String modeType;


    @Override
    public void run(String... args) {
       if (modeType.equals("always")){
           Permission[] values = Permission.values();

           Role superAdmin = roleRepository.save(new Role(RoleBasic.SUPER_ADMIN.name(), Arrays.asList(values), "SuperAdmin"));

           Role manager = roleRepository.save(new Role(RoleBasic.MANAGER.name(),
                   Arrays.asList(
                           Permission.ADD_TASKS,
                           Permission.GET_COMPANY,
                           Permission.GET_DEPARTMENT,
                           Permission.EDIT_TASKS,
                           Permission.GET_TASKS,
                           Permission.GET_USER
                   ),
                   "manager"));

           Role director = roleRepository.save(new Role(RoleBasic.DIRECTOR.name(),
                   Arrays.asList(
                           Permission.GET_TASKS
                   ),
                   "director"));
           Role user = roleRepository.save(new Role(RoleBasic.USER.name(),
                   Arrays.asList(
                           Permission.GET_COMPANY,
                           Permission.GET_DEPARTMENT,
                           Permission.EDIT_TASKS,
                           Permission.GET_TASKS,
                           Permission.GET_USER
                   ),
                   "user"));

           roleRepository.save(superAdmin);
           Role saveManager = roleRepository.save(manager);
           roleRepository.save(director);
           roleRepository.save(user);

           User admin = new User("Super Admin", "superadmin", passwordEncoder.encode("SuperAdmin2023"), superAdmin, true);
           User adminSave = userRepository.save(admin);

           Company company = new Company("Pixel","IT company");
           company.setCreatedBy(adminSave);

           Company saveCompany = companyRepository.save(company);

           Department department = new Department("HR","Hr", saveCompany);
           department.setCreatedBy(adminSave);
           Department deparmentSaved = departmentRepository.save(department);

           Department department2 = new Department("Saller","Saller", saveCompany);
           department.setCreatedBy(adminSave);
           Department deparmentSaved2 = departmentRepository.save(department2);

           Department department3 = new Department("IOS","IOS", saveCompany);
           department.setCreatedBy(adminSave);
           Department deparmentSaved3 = departmentRepository.save(department3);


//           User manager1 = new User("Manager", "manager", passwordEncoder.encode("manager1"), saveManager, true);
//           manager1.setDepartment(deparmentSaved);
//           manager1.setCreatedBy(adminSave);
//           User manager1Save = userRepository.save(manager1);
//
//
//           User manager2 = new User("Manager2", "manager2", passwordEncoder.encode("manager2"), manager, true);
//           manager2.setDepartment(deparmentSaved2);
//           manager2.setCreatedBy(adminSave);
//           User manager2Save = userRepository.save(manager2);
//
//           User manager3 = new User("Manager3", "manager3", passwordEncoder.encode("manager3"), manager, true);
//           manager3.setDepartment(deparmentSaved3);
//           manager3.setCreatedBy(adminSave);
//           User manager3Save = userRepository.save(manager3);
//
//
//           User savedDirector = userRepository.save(new User("Director", "director", passwordEncoder.encode("director2023"), director, true));
//           savedDirector.setDepartment(deparmentSaved);
//           savedDirector.setCreatedBy(adminSave);
//           userRepository.save(manager1);
//
//
//           User savedUser = userRepository.save(new User("User", "user", passwordEncoder.encode("user2023"), user, true));
//
//           telegramUser.setUser(adminSave);
//           TelegramUser manager1Tg = new TelegramUser("manager1","manager1",12313213123L,true);
//           manager1Tg.setUser(manager1Save);
//           TelegramUser manager2Tg = new TelegramUser("manager2","manager2",1221321213313213123L,true);
//           manager2Tg.setUser(manager2Save);
//           TelegramUser manager3Tg = new TelegramUser("manager3","manager3",1221321213313213323L,true);
//           manager3Tg.setUser(manager3Save);

//           telegramUserRepository.save(manager1Tg);
//           telegramUserRepository.save(manager2Tg);
//           telegramUserRepository.save(manager3Tg);






       }
    }
}
