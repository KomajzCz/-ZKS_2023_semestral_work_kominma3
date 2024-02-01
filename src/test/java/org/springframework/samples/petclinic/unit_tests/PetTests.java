package org.springframework.samples.petclinic.unit_tests;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PetTests {

	private Validator createValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.afterPropertiesSet();
		return localValidatorFactoryBean;
	}

	@Test
	public void test_create_new_pet_with_valid_inputs_successfully() {
		Pet pet = new Pet();
		PetType petType = new PetType();
		petType.setName("Antilopa");
		pet.setName("Ruby");
		pet.setType(petType);
		pet.setBirthDate(LocalDate.now());

		Validator validator = createValidator();
		Set<ConstraintViolation<Pet>> constraintViolations = validator.validate(pet);

		// Assert that there are no errors
		assertTrue(constraintViolations.isEmpty());

		// Assert that pet has required type and birthdate
		assertEquals("Ruby", pet.getName());
		assertEquals(petType, pet.getType());
		assertEquals(LocalDate.now(), pet.getBirthDate());
	}

	@Test
	public void test_addVisit_with_valid_visit_successfully() {
		Pet pet = new Pet();
		pet.setName("Maxipes Fik");
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());

		pet.addVisit(visit);

		// Assert that pet contains required visit
		assertTrue(pet.getVisits().contains(visit));
	}

}
