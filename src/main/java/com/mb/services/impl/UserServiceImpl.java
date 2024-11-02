package com.mb.services.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mb.entities.User;
import com.mb.helpers.ResourceNotFoundException;
import com.mb.repositories.UserRepo;
import com.mb.services.UserService;
import com.mb.helpers.AppConstants;
import com.mb.helpers.FileCRUD;

import jakarta.persistence.GenerationType;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public User saveUser(User user) {
//		LocalDate birthDate = user.getDateOfBirth();
//		LocalDate currentDate = LocalDate.now();
//		int age = Period.between(birthDate, currentDate).getYears();
//		user.setDateOfBirth(birthDate);
//		user.setAge(age);

// 		User ID : have to generate...
//		String userId = UUID.randomUUID().toString();
//		user.setUserId(userId);
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// set the user role
		user.setRoleList(List.of(AppConstants.ROLE_USER));

		return userRepo.save(user);
	}

	@Override
	public Optional<User> getUserById(Long id) {
		return userRepo.findById(id);
	}

	@Override
	public Optional<User> updateUser(User user) {

		User oldUser = userRepo.findById(user.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Update karenge oldUser from User
		oldUser.setGender(user.getGender());
		oldUser.setReligion(user.getReligion());
		oldUser.setCaste(user.getCaste());
		oldUser.setMaxAge(user.getMaxAge());
		oldUser.setMaxHeight(user.getMaxHeight());
		oldUser.setMarriedStatus(user.getMarriedStatus());
		oldUser.setPlace(user.getPlace());
		oldUser.setOccupation(user.getOccupation());

		// save the user in database
		User save = userRepo.save(oldUser);
		return Optional.ofNullable(save);
	}

	@Override
	public void deleteUserById(Long id) {
		User user2 = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		userRepo.delete(user2);
	}

	@Override
	public void deleteUser(User user) {
		userRepo.delete(user);
	}

	@Override
	public boolean isUserExist(Long userId) {
		User user2 = userRepo.findById(userId).orElse(null);
		return user2 != null ? true : false;
	}

	@Override
	public boolean isUserExistByEmail(String email) {
		User user = userRepo.findByEmail(email).orElse(null);
		return user != null ? true : false;
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email).orElse(null);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

//	@Override
//    public List<User> getUsersByAgeRange(int minAge, int maxAge) {
//        if (minAge > maxAge) {
//            throw new IllegalArgumentException("minAge cannot be greater than maxAge");
//        }
//        return userRepo.findByAgeBetween(minAge, maxAge);
//    }	

	public List<String> getAllDistinctReligions() {
		return userRepo.findDistinctReligion();
	}
	public List<String> getAllDistinctCastes(String religion) {
		return userRepo.findDistinctCaste(religion);
	}

	@Override
	public List<User> findMatchUserDetails(User user) {
		String gender = user.getGender();
		String religion = user.getReligion();
		String caste = user.getCaste();
		int minAge = user.getMinAge();
		int maxAge = user.getMaxAge();
		int minheight = user.getMinHeight();
		int maxheight = user.getMaxHeight();
		String marriedStatus = user.getMarriedStatus();
		String place = user.getPlace();
		String occupation = user.getOccupation();

		return userRepo.findUsersByCustomCriterialist(gender, religion, caste, minAge, maxAge, minheight, maxheight,
				marriedStatus, place, occupation);
	}

	@Override
	public Page<User> findMatchUserDetailsByFilter(User user, int page, int size, String sortBy, String direction) {
		String gender = user.getGender();
		String religion = user.getReligion();
		String caste = user.getCaste();
		int minAge = user.getMinAge();
		int maxAge = user.getMaxAge();
		int minheight = user.getMinHeight();
		int maxheight = user.getMaxHeight();
		String marriedStatus = user.getMarriedStatus();
		String place = user.getPlace();
		String occupation = user.getOccupation();

		Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		return userRepo.findUsersByCustomCriteria(gender, religion, caste, minAge, maxAge, minheight, maxheight,
				marriedStatus, place, occupation, pageable);
	}

	@Override
	public Page<User> getByUser(int page, int size, String sortBy, String direction) {

		Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return userRepo.findAll(pageable);
	}

	public void saveFile(MultipartFile file) {

		try {
			List<User> users = FileCRUD.convertExcelToListOfUser(file.getInputStream());
			this.userRepo.saveAll(users);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
