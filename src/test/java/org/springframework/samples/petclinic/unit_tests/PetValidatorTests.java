package org.springframework.samples.petclinic.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetValidator;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PetValidatorTests {

	private PetValidator validator;

	private Pet pet;

	private Errors errors;

	@BeforeEach
	public void setUp() {
		validator = new PetValidator();
		pet = new Pet();
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	public void test_validate_successfully() {
		pet.setName("Scooby Doo");
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());
		validator.validate(pet, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void test_validate_pet_with_empty_name_should_throw_error() {
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());
		pet.setName("");
		validator.validate(pet, errors);
		// Assert that there is 1 error with name
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertTrue(errors.hasFieldErrors("name"));
	}

	@Test
	public void test_validate_null_pet_type_should_throw_error() {
		pet.setName("Scooby Doo");
		pet.setBirthDate(LocalDate.now());
		validator.validate(pet, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertTrue(errors.hasFieldErrors("type"));
	}

	@Test
	public void test_validate_pet_with_null_birthdate_should_throw_error() {
		pet.setName("Scooby Doo");
		pet.setType(new PetType());
		validator.validate(pet, errors);
		assertTrue(errors.hasErrors());
		assertEquals(1, errors.getErrorCount());
		assertTrue(errors.hasFieldErrors("birthDate"));
	}

}
