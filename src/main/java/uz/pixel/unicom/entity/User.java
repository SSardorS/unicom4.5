package uz.pixel.unicom.entity;


import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import uz.pixel.unicom.enam.Permission;
import uz.pixel.unicom.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "unicom_users")
public class User extends AbsUUIDEntity implements UserDetails {

    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @ManyToOne( optional = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Department department;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TelegramUser telegramUser;

    private boolean enabled;

    private int restartPasswordCode;

    private int deleteAccauntCode;

    private int registerCode;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Permission> roleList = this.role.getRoleList();
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Permission permission : roleList) {
            grantedAuthorities.add(new SimpleGrantedAuthority(permission.name()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public User(String fullName, String username, String password, Role role, boolean enabled) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public User(String fullName, String username, String password, Role role, boolean enabled, int restartPasswordCode) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.registerCode = restartPasswordCode;
    }

    public User(String fullName, String username, String password, Role role, boolean enabled, int restartPasswordCode, int deleteAccauntCode, int registerCode) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.restartPasswordCode = restartPasswordCode;
        this.deleteAccauntCode = deleteAccauntCode;
        this.registerCode = registerCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


    public static User getuser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
