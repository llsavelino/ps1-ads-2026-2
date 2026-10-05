package br.edu.fatecfranca.api.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import br.edu.fatecfranca.api.controllers.dtos.CustomerDto;
import br.edu.fatecfranca.api.controllers.reqs.CustomerReq;
import br.edu.fatecfranca.api.controllers.reqs.ValidationGroups;
import br.edu.fatecfranca.api.entities.Customers;
import br.edu.fatecfranca.api.services.CustomersService;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;
import br.edu.fatecfranca.api.services.policy.contracts.PageMeta;

@RestController @RequestMapping("/customers")
public class CustomerController {

    private final CustomersService service;

    public CustomerController(CustomersService service) { this.service = service; }

    private boolean exists(Long id) { return this.service.existsById(id); }

    @PostMapping
    public ResponseEntity<Contract<Long>> create(
            @Validated(ValidationGroups.Required.class) @RequestBody CustomerReq customerReq
    ) {
        Contract<Long> body = Contract.created(this.service.create(customerReq.toEntity()).getId());
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract<CustomerDto>> update(
            @PathVariable Long id,
            @Validated(ValidationGroups.Required.class) @RequestBody CustomerReq customerReq
    ) {
        if (!exists(id)) {
            Contract<CustomerDto> body = Contract.notFound("CUSTOMER_NOT_FOUND", "Customer not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        Customers customer = customerReq.toEntity();
        customer.setId(id);
        Contract<CustomerDto> body = Contract.ok(new CustomerDto(this.service.update(customer)));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @GetMapping
    public ResponseEntity<Contract<Iterable<CustomerDto>>> getAll(Pageable pageable) {
        Page<CustomerDto> page = this.service.findAll(pageable).map(CustomerDto::new);
        Contract<Iterable<CustomerDto>> body = Contract.okPage(
            page.getContent(), PageMeta.of(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages())
        );
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract<CustomerDto>> find(@PathVariable Long id) {
        Contract<CustomerDto> body = this.service.findById(id)
                .map(customer -> Contract.ok(new CustomerDto(customer)))
                .orElse(Contract.notFound("CUSTOMER_NOT_FOUND", "Customer not found"));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Contract<Void>> delete(@PathVariable Long id) {
        if (!exists(id)) {
            Contract<Void> body = Contract.notFound("CUSTOMER_NOT_FOUND", "Customer not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        this.service.deleteById(id);
        return ResponseEntity.status(HttpStatus.valueOf(Contract.noContent().getHttpStatus())).body(Contract.noContent());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contract<CustomerDto>> patch(
            @PathVariable Long id,
            @Validated @RequestBody CustomerReq customerReq
    ) {
        var opt = this.service.findById(id);
        if (opt.isEmpty()) {
            Contract<CustomerDto> body = Contract.notFound("CUSTOMER_NOT_FOUND", "Customer not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        if (!customerReq.hasAnyField()) {
            Contract<CustomerDto> body = Contract.badRequest("BAD_REQUEST", "no fields provided for update");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        Customers c = opt.get();
        if (customerReq.getName() != null) c.setName(customerReq.getName());
        if (customerReq.getIdentDocument() != null) c.setIdentDocument(customerReq.getIdentDocument());
        if (customerReq.getBirthDate() != null) c.setBirthDate(customerReq.getBirthDate());
        if (customerReq.getStreetName() != null) c.setStreetName(customerReq.getStreetName());
        if (customerReq.getHouseNumber() != null) c.setHouseNumber(customerReq.getHouseNumber());
        if (customerReq.getComplements() != null) c.setComplements(customerReq.getComplements());
        if (customerReq.getDistrict() != null) c.setDistrict(customerReq.getDistrict());
        if (customerReq.getMunicipality() != null) c.setMunicipality(customerReq.getMunicipality());
        if (customerReq.getState() != null) c.setState(customerReq.getState());
        if (customerReq.getPhone() != null) c.setPhone(customerReq.getPhone());
        if (customerReq.getEmail() != null) c.setEmail(customerReq.getEmail());
        Contract<CustomerDto> body = Contract.ok(new CustomerDto(this.service.update(c)));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }
}
