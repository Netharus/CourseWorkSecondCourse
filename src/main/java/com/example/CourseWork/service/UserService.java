package com.example.CourseWork.service;

import com.example.CourseWork.entity.Role;
import com.example.CourseWork.entity.User;
import com.example.CourseWork.exceptions.UserNotFoundException;
import com.example.CourseWork.repository.RoleRepository;
import com.example.CourseWork.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    public static final int USERS_PER_PAGE=10;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    public List<User> listAll(){
        return (List<User>) userRepo.findAll();
    }

    public Page<User> listByPage(int pageNum,String sortField,String sortDir,String keyword){
        Sort sort= Sort.by(sortField);
        sort=sortDir.equals("asc")? sort.ascending():sort.descending();

        Pageable pageable= PageRequest.of(pageNum-1,USERS_PER_PAGE,sort);

        if(keyword !=null){
             return userRepo.findAll(keyword,pageable);
        }
        return userRepo.findAll(pageable);
    }
    public List<Role> listRoles(){
        return (List<Role>) roleRepo.findAll();
    }

    public User getByEmail(String email){
        return userRepo.getUserByEmail(email);
    }
    public void save(User user) {
        boolean isUpdatingUser=(user.getId()!=null);

        if(isUpdatingUser){
            User existingUser=userRepo.findById(user.getId()).get();
            if (user.getPassword()==null){
                user.setPassword(existingUser.getPassword());
            }else
            if(user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }else{
                encodePassword(user);
            }
        }else{
        encodePassword(user);}
        userRepo.save(user);
    }
    public void savePassword(User user) {
        if(!user.getPassword().isEmpty()){
        User existingUser=userRepo.findById(user.getId()).get();
        existingUser.setPassword(user.getPassword());
        encodePassword(existingUser);
        userRepo.save(existingUser);}
    }
    public void updateUser(User user){

        User existingUser=userRepo.findById(user.getId()).get();
        if (user.getPassword()==null){
            user.setPassword(existingUser.getPassword());
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPatronymic(user.getPatronymic());
        userRepo.save(existingUser);
    }
    public void encodePassword(User user){
        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public boolean isEmailUnique(Long id,String email){
        User userByEmail=userRepo.getUserByEmail(email);
        System.out.println(userByEmail);
        if(userByEmail==null){return true;
        }
        boolean isCreatingNew=(id==null);
        if(isCreatingNew){
            if(userByEmail!=null) return false;
        }else {
            if(userByEmail.getId()!=id){
                return false;
            }
        }
        return true;
    }

    public User get(Long id) throws UserNotFoundException {
        try{
        return userRepo.findById(id).get();
    }catch(NoSuchElementException ex){
            throw new UserNotFoundException("Не может найти пользователя с таким ID" + id);
        }
    }
    public void delete(Long id) throws UserNotFoundException {
        Long countById= userRepo.countById(id);
        if(countById==null||countById==0){
            throw new UserNotFoundException("Не может найти пользователя с таким ID" + id);
        }
        userRepo.deleteById(id);
    }

    public void changeStatus(Long id) throws UserNotFoundException {
        Long countById= userRepo.countById(id);
        if(countById==null||countById==0){
            throw new UserNotFoundException("Не может найти пользователя с таким ID" + id);
        }
        User existingUser=userRepo.findById(id).get();
        if (existingUser.getEnabled()) {
            existingUser.setEnabled(false);
        } else {
            existingUser.setEnabled(true);
        }
        userRepo.save(existingUser);
    }

}
