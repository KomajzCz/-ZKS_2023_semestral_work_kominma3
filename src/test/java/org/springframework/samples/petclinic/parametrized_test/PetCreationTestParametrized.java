package org.springframework.samples.petclinic.parametrized_test;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class PetCreationTestParametrized {
	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private String parseInput(String input) {
		if (input.equals("null"))
			return null;
		if (input.equals("empty"))
			return "";
		return input;
	}

	@ParameterizedTest
	@CsvFileSource(files = "src/test/resources/pet_creation_output.csv", numLinesToSkip = 1)
	void test_create_new_pet(String name, String type, String birthdate, boolean correct) {
		Owner owner = Mockito.mock(Owner.class);
		ModelMap model = new ModelMap();
		BindingResult result = new MapBindingResult(new HashMap<>(), "binding");

		// Create a PetController object with a mock OwnerRepository
		OwnerRepository ownerRepository = Mockito.mock(OwnerRepository.class);
		PetController petController = new PetController(ownerRepository);

		// Create pet type from given parameter
		PetType inputPetType = new PetType();

		String parsedType = parseInput(type);
		if(parsedType != null)
		{
			inputPetType.setName(parsedType);
		}
		else
		{
			inputPetType.setName(null);
		}

		// Create new Pet object with given parameters
		Pet pet = new Pet();
		pet.setName(parseInput(name));
		pet.setType(inputPetType);
		String parsedBirthdate = parseInput(birthdate);
		if (parsedBirthdate != null) {
			try {
				LocalDate new_date = LocalDate.parse(parsedBirthdate, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
				pet.setBirthDate(new_date);
			}
			catch (Exception e) {
                Assertions.assertFalse(correct);
				return;
			}
		}
		else {
			pet.setBirthDate(null);

		}
		owner.addPet(pet);

		Mockito.when(owner.getPet(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(null);
		String expectedResult = correct ? "redirect:/owners/{ownerId}" : VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		String resultString = petController.processCreationForm(owner, pet, result, model);
		Assertions.assertEquals(expectedResult, resultString);
	}

}

