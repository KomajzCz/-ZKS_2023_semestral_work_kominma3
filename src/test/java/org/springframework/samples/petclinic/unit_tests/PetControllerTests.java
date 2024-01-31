package org.springframework.samples.petclinic.unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.samples.petclinic.owner.*;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PetControllerTests {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";

	private Owner owner;

	private Owner mockedOwner;

	private BindingResult result;

	private ModelMap model;


	private OwnerRepository ownerRepository;

	private PetController petController;

	private Map<String, PetType> petTypes;

	@BeforeEach
	public void setUp() {
		owner = new Owner();
		owner.setFirstName("Shaggy");
		owner.setLastName("Rogers");
		owner.setAddress("Mystery, Inc.");
		owner.setCity("Crystal Cove");
		owner.setTelephone("1239875670");
		mockedOwner = mock(Owner.class);
		ownerRepository = mock(OwnerRepository.class);
		petController = new PetController(ownerRepository);
		model = new ModelMap();
		result = new MapBindingResult(new HashMap<>(), "binding");

		// testing petTypes
		List<String> types = Arrays.asList("dog", "bird", "cat", "hamster", "lizard", "snake");
		petTypes = new HashMap<>();
		for (String type : types) {
			PetType petType = new PetType();
			petType.setName(type);
			petTypes.put(type, petType);
		}
	}

	@Test
	@Tag("success")
	public void test_findOwner_should_return_owner_with_given_id_successfully() {
		int ownerId = 1;
		owner.setId(1);
		Mockito.when(ownerRepository.findById(ownerId)).thenReturn(owner);
		Owner actualOwner = petController.findOwner(ownerId);
		assertEquals(owner, actualOwner);
	}
	@Test
	@Tag("fail")
	public void test_findOwner_should_throw_exception_owner_not_found() {
		int ownerId = 1;
		Mockito.when(ownerRepository.findById(Mockito.anyInt())).thenReturn(null);
		Exception e = assertThrows(IllegalArgumentException.class, () -> {
			petController.findOwner(ownerId);
		});
		assertEquals("Owner ID not found: " + ownerId, e.getMessage());

	}
	@Test
	@Tag("success")
	public void test_findPet_returns_existing_pet_successfully() {
		mockedOwner.setId(1);
		Pet pet = new Pet();
		pet.setId(1);
		Mockito.when(ownerRepository.findById(1)).thenReturn(mockedOwner);
		Mockito.when(mockedOwner.getPet(pet.getId())).thenReturn(pet);
		Pet result = petController.findPet(1, 1);
		assertEquals(pet, result);
	}
	@Test
	@Tag("success")
	public void test_findPet_returns_new_pet_object() {
		owner.setId(1);
		Mockito.when(ownerRepository.findById(1)).thenReturn(owner);
		Pet result = petController.findPet(1, null);
		assertNotNull(result);
		assertTrue(result.isNew());
	}

	@Test
	@Tag("success")
	public void test_initCreationForm_with_valid_owner_object() {
		String resultString = petController.initCreationForm(owner, model);
		assertEquals(VIEWS_PETS_CREATE_OR_UPDATE_FORM, resultString);
		assertNotNull(model.get("pet"));
		assertTrue(((Pet) model.get("pet")).isNew());
	}
	@Test
	@Tag("success")
	public void test_processCreationForm_new_pet_successfully() {
		Pet pet = new Pet();
		pet.setName("Scooby Doo");
		pet.setBirthDate(LocalDate.now());
		PetType petType = new PetType();
		petType.setName("dog");
		pet.setType(petType);
		String resultString = petController.processCreationForm(owner, pet, result, model);
		assertEquals("redirect:/owners/{ownerId}", resultString);
		assertFalse(result.hasErrors());
		assertTrue(owner.getPets().contains(pet));
	}

	@Test
	@Tag("fail")
	public void test_processCreationForm_duplicate_pet_should_return_error() {
		Pet pet = new Pet();
		pet.setName("Scooby Doo");
		pet.setBirthDate(LocalDate.now());
		PetType petType = new PetType();
		petType.setName("dog");
		pet.setType(petType);
		String resultFirstPet = petController.processCreationForm(mockedOwner, pet, result, model);
		Mockito.when(mockedOwner.getPet(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(pet);
		//Create another pet with same name
		Pet newPet = new Pet();
		newPet.setName("Scooby Doo");
		newPet.setBirthDate(LocalDate.now());
		PetType petType2 = new PetType();
		petType2.setName("bird");
		newPet.setType(petType2);
		String resultSecondPet = petController.processCreationForm(mockedOwner, newPet, result, model);
		assertEquals("redirect:/owners/{ownerId}", resultFirstPet);
		assertEquals(VIEWS_PETS_CREATE_OR_UPDATE_FORM, resultSecondPet);
		assertTrue(result.hasErrors());
		assertNotNull(result.getFieldError("name"));
	}

	@Test
	@Tag("success")
	public void test_processCreationForm_add_pet_successfully() {
		Pet pet = new Pet();
		pet.setName("Scooby Doo");
		pet.setBirthDate(LocalDate.now());
		PetType petType = new PetType();
		petType.setName("dog");
		pet.setType(petType);
		String resultString = petController.processCreationForm(owner, pet, result, model);
		assertEquals("redirect:/owners/{ownerId}", resultString);
		assertTrue(owner.getPets().contains(pet));
	}

	@Test
	@Tag("success")
	public void test_initUpdateForm_with_valid_pet_object() {
		Pet pet = new Pet();
		pet.setName("Scooby Doo");
		PetType petType = new PetType();
		petType.setName("dog");
		pet.setType(petType);
		pet.setBirthDate(LocalDate.now());
		mockedOwner.addPet(pet);
		Mockito.when(mockedOwner.getPet(Mockito.anyInt())).thenReturn(pet);
		String resultString = petController.initUpdateForm(mockedOwner, 1, model);
		assertEquals(VIEWS_PETS_CREATE_OR_UPDATE_FORM, resultString);
		assertEquals(pet, model.get("pet"));
	}

	@Test
	@Tag("success")
	public void test_processUpdateForm_set_pet_new_name_successfully() {
		Pet pet = new Pet();
		pet.setName("Scooby Doo");
		Mockito.when(mockedOwner.getPet(pet.getName().toLowerCase(), false)).thenReturn(null);
		String resultString = petController.processUpdateForm(pet, result, mockedOwner, model);
		assertEquals("redirect:/owners/{ownerId}", resultString);
		Mockito.verify(mockedOwner).addPet(pet);
		Mockito.verify(ownerRepository).save(mockedOwner);
	}

	@Test
	@Tag("fail")
	public void test_processUpdateForm_set_pet_new_name_return_duplicate_error() {
		Pet pet = new Pet();
		pet.setId(1);
		pet.setName("Scooby Doo");
		PetType petType = new PetType();
		petType.setName("dog");
		pet.setType(petType);
		pet.setBirthDate(LocalDate.now());
		// new pet which we want to add
		Pet newPet = new Pet();
		newPet.setName("Scooby Doo");
		newPet.setType(petTypes.get("lizard"));
		PetType newPetType = new PetType();
		newPetType.setName("dog");
		newPet.setType(newPetType);
		newPet.setBirthDate(LocalDate.now());
		Mockito.when(mockedOwner.getPet(newPet.getName().toLowerCase(), false)).thenReturn(pet);
		String resultString = petController.processUpdateForm(newPet, result, mockedOwner, model);
		assertEquals(VIEWS_PETS_CREATE_OR_UPDATE_FORM, resultString);
		assertTrue(result.hasErrors());
	}















}
