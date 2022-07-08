package com.example.demo;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;

/**
 * Demonstration of REST service using Spring Framework.
 * 
 * The tutorial guides the construction of a simple web app and then updates the
 * implementation to highlight the key components of a RESTful service.
 * 
 * SUPPORTING CHANGES TO THE API -- "never delete a column in a database" - Unknown
 * To support updates to old and new clients while minimizing downtime, add new fields to 
 * JSON representation AND display / process incoming data BOTH ways.
 * This implementation began with just a NAME field and was updated to support FIRST NAME and 
 * LAST NAME fields.
 * 
 * @author https://spring.io/guides/tutorials/rest/
 *
 */
@RestController
class EmployeeController {

	/** Database of Employee resources */
	private final EmployeeRepository repository;

	/** Instance of EmployeeModelAssembler to convert Employee int EntityModel<Employee> */
	private final EmployeeModelAssembler assembler;

	EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {

	    this.repository = repository;
	    this.assembler = assembler;
	}


  // Aggregate root
  // tag::get-aggregate-root[]
	@GetMapping("/employees")
	CollectionModel<EntityModel<Employee>> all() {

	  List<EntityModel<Employee>> employees = repository.findAll().stream()
	      .map(assembler::toModel)
	      .collect(Collectors.toList());

	  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
	}
  // end::get-aggregate-root[]


  //@PostMapping("/employees")
  //Employee newEmployee(@RequestBody Employee newEmployee) {
  //  return repository.save(newEmployee);
  //}
	
	@PostMapping("/employees")
	ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {

	  EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

	  return ResponseEntity //
	      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
	      .body(entityModel);
	}

  // Single item
  
  /**
   * Getting a single item employee delegated to the 
   * EmployeeModelAssembler
   * 
   * @param id Employee id
   * @return 
   */
  @GetMapping("/employees/{id}")
  EntityModel<Employee> one(@PathVariable Long id) {

    Employee employee = repository.findById(id) //
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    return assembler.toModel(employee);
  }

  //@PutMapping("/employees/{id}")
  //Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
  //  
  //  return repository.findById(id)
  //    .map(employee -> {
  //      employee.setName(newEmployee.getName());
  //      employee.setRole(newEmployee.getRole());
  //      return repository.save(employee);
  //    })
  //    .orElseGet(() -> {
  ///      newEmployee.setId(id);
  //      return repository.save(newEmployee);
  //    });
  //}
  @PutMapping("/employees/{id}")
  ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

    Employee updatedEmployee = repository.findById(id) //
        .map(employee -> {
          employee.setName(newEmployee.getName());
          employee.setRole(newEmployee.getRole());
          return repository.save(employee);
        }) //
        .orElseGet(() -> {
          newEmployee.setId(id);
          return repository.save(newEmployee);
        });

    EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

    return ResponseEntity //
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
        .body(entityModel);
  }

  //@DeleteMapping("/employees/{id}")
  //void deleteEmployee(@PathVariable Long id) {
  //  repository.deleteById(id);
  //}
  @DeleteMapping("/employees/{id}")
  ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

    repository.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}