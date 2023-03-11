package uz.pixel.unicom.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pixel.unicom.payload.DepartmentDto;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.service.DepartmentService;

import java.util.UUID;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/department")
public class DeparmentController {

    @Autowired
    DepartmentService departmentService;


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @PostMapping
    public HttpEntity<?> add(@RequestBody DepartmentDto departmentDto){

        ResponseMessage add = departmentService.add(departmentDto);
        return ResponseEntity.status(add.getStatus()).body(add);

    }

    @PreAuthorize("hasAuthority('GET_EVERY_THINGS')")
    @GetMapping
    public HttpEntity<?> getAll(@RequestParam int pageNumber){

        ResponseMessage getAll = departmentService.getAll(pageNumber);
        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @GetMapping("/{id}")
    public HttpEntity<?> getById(@PathVariable String id){

        ResponseMessage getById = departmentService.getById(UUID.fromString(id));
        return ResponseEntity.status(getById.getStatus()).body(getById);

    }


    @PreAuthorize("hasAuthority('ADD_EVERY_THINGS')")
    @PutMapping
    public HttpEntity<?> edit(@RequestBody DepartmentDto departmentDto){

        ResponseMessage add = departmentService.edit(departmentDto);
        return ResponseEntity.status(add.getStatus()).body(add);

    }

}
