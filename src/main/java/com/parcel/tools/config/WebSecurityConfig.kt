package com.parcel.tools.config;


//import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter

import com.parcel.tools.Globals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var dataSource: DataSource



    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        println("CONFIGURE(http: HttpSecurity)!!!")
        http
                .authorizeRequests()
                .antMatchers(  "/web/css/*",  "/web/javascript/*",
                        "/web/javascript/games/*", "/web/javascript/games/mafia/*").permitAll()
                .antMatchers("/", "/utils", "/etools",
                        "/games", "/games/*").permitAll()
                .antMatchers("/admin").hasAuthority("ADMIN")
                .antMatchers("/games_settings", "/games_settings/*").hasAnyAuthority("ADMIN", "USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()


    }




    override fun configure(auth: AuthenticationManagerBuilder) {
        println("CONFIGURE!!!")
        printUsers()
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("SELECT login, password, active FROM users WHERE login =?")
                //.authoritiesByUsernameQuery("SELECT users.login, roles.user_role FROM roles_to_users " +
                //        "JOIN users ON users.id = roles_to_users.users_id " +
                //        "JOIN roles ON roles.id = roles_to_users.roles_id " +
                //        "WHERE users.login = ?")
        .authoritiesByUsernameQuery("SELECT user_login, user_role FROM get_user_and_role( ? )")
    }

    private fun printUsers()
    {
        val users =Globals.userManager.getAllUsers()
        users.forEach {
            println("$it\n")
        }
    }




}



