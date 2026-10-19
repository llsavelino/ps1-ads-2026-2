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

import br.edu.fatecfranca.api.controllers.pipes.dtos.CarDto;
import br.edu.fatecfranca.api.controllers.pipes.reqs.CarReq;
import br.edu.fatecfranca.api.controllers.pipes.reqs.ValidationGroups;
import br.edu.fatecfranca.api.entities.Cars;
import br.edu.fatecfranca.api.entities.Customers;
import br.edu.fatecfranca.api.services.CarsService;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata.PageMeta;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;

@RestController @RequestMapping("/cars")
public class CarController {

    private final CarsService service;

    public CarController(CarsService service) { this.service = service; }

    private boolean exists(Long id) { return this.service.existsById(id); }

    @PostMapping
    public ResponseEntity<Contract<Long>> create(
            @Validated(ValidationGroups.Required.class) @RequestBody CarReq carReq
    ) {
        Contract<Long> body = Contract.created(this.service.create(carReq.toEntity()).getId());
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract<CarDto>> update(
            @PathVariable Long id,
            @Validated(ValidationGroups.Required.class) @RequestBody CarReq carReq
    ) {
        if (!exists(id)) {
            Contract<CarDto> body = Contract.notFound("CAR_NOT_FOUND", "Car not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        Cars car = carReq.toEntity();
        car.setId(id);
        Contract<CarDto> body = Contract.ok(new CarDto(this.service.update(car)));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @GetMapping
    public ResponseEntity<Contract<Iterable<CarDto>>> getAll(Pageable pageable) {
        Page<CarDto> page = this.service.findAll(pageable).map(CarDto::new);
        Contract<Iterable<CarDto>> body = Contract.okPage(
            page.getContent(), PageMeta.of(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages())
        );
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract<CarDto>> find(@PathVariable Long id) {
        Contract<CarDto> body = this.service.findById(id)
                .map(car -> Contract.ok(new CarDto(car)))
                .orElse(Contract.notFound("CAR_NOT_FOUND", "Car not found"));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Contract<Void>> delete(@PathVariable Long id) {
        if (!exists(id)) {
            Contract<Void> body = Contract.notFound("CAR_NOT_FOUND", "Car not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        this.service.deleteById(id);
        return ResponseEntity.status(HttpStatus.valueOf(Contract.noContent().getHttpStatus())).body(Contract.noContent());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contract<CarDto>> patch(
            @PathVariable Long id,
            @Validated @RequestBody CarReq carReq
    ) {
        var opt = this.service.findById(id);
        if (opt.isEmpty()) {
            Contract<CarDto> body = Contract.notFound("CAR_NOT_FOUND", "Car not found");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }
        if (!carReq.hasAnyField()) {
            Contract<CarDto> body = Contract.badRequest("BAD_REQUEST", "no fields provided for update");
            return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
        }

        Cars c = opt.get();
        if (carReq.getBrand() != null) c.setBrand(carReq.getBrand());
        if (carReq.getModel() != null) c.setModel(carReq.getModel());
        if (carReq.getColor() != null) c.setColor(carReq.getColor());
        if (carReq.getYearManufacture() != null) c.setYearManufacture(carReq.getYearManufacture());
        if (carReq.getImported() != null) c.setImported(carReq.getImported());
        if (carReq.getPlates() != null) c.setPlates(carReq.getPlates());
        if (carReq.getSellingDate() != null) c.setSellingDate(carReq.getSellingDate());
        if (carReq.getSellingPrice() != null) c.setSellingPrice(carReq.getSellingPrice());
        if (carReq.getCustomerId() != null) {
            Customers customer = new Customers();
            customer.setId(carReq.getCustomerId());
            c.setCustomer(customer);
        }

        Contract<CarDto> body = Contract.ok(new CarDto(this.service.update(c)));
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }
}
