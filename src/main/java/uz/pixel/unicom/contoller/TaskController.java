package uz.pixel.unicom.contoller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pixel.unicom.enam.Status;
import uz.pixel.unicom.payload.ResponseMessage;
import uz.pixel.unicom.payload.TaskChangeStepDto;
import uz.pixel.unicom.payload.TaskDto;
import uz.pixel.unicom.service.TaskService;

import java.util.UUID;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/task")
public class TaskController {


    @Autowired
    TaskService taskService;

    /**
     *
     * Super Admin Systeam
     *
     */

    @PreAuthorize("hasAuthority('ADD_TASKS')")
    @PostMapping
    public HttpEntity<?> add(@RequestBody TaskDto taskDto){

        ResponseMessage add = taskService.add(taskDto);

        return ResponseEntity.status(add.getStatus()).body(add);

    }

    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/all")
    public HttpEntity<?> getAll(@RequestParam int pageNumber){

        ResponseMessage add = taskService.getAll(pageNumber);

        return ResponseEntity.status(add.getStatus()).body(add);

    }

    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/new")
    public HttpEntity<?> getAllNew(@RequestParam int pageNumber){

        ResponseMessage getAll = taskService.getAllNew(pageNumber, Status.NEW);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }

    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/inprogress")
    public HttpEntity<?> getAllSendToManager(@RequestParam int pageNumber){

        ResponseMessage getAll = taskService.getInProgress(pageNumber);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }

//    @PreAuthorize("hasAuthority('GET_EVERY_THINGS')")
//    @GetMapping("/admin/touser")
//    public HttpEntity<?> getAllSendToUser(@RequestParam int pageNumber){
//
//        ResponseMessage getAll = taskService.getAllForSuperAdmin(pageNumber, Status.SEND_TO_USER);
//
//        return ResponseEntity.status(getAll.getStatus()).body(getAll);
//
//    }


    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/finished")
    public HttpEntity<?> getFinished(@RequestParam int pageNumber){

        ResponseMessage getAll = taskService.getFinishedAll(pageNumber, Status.FINISHED);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }




//    @PreAuthorize("hasAnyAuthority('GET_EVERY_THINGS')")
//    @GetMapping("/admin/{code}")
//    public HttpEntity<?> getById(@PathVariable String code){
//
//        ResponseMessage getAll = taskService.getById(code);
//
//        return ResponseEntity.status(getAll.getStatus()).body(getAll);
//
//    }

    @PreAuthorize("hasAuthority('EDIT_TASKS')")
    @PutMapping
    public HttpEntity<?> edit(@RequestBody TaskDto taskDto){

        ResponseMessage getAll = taskService.edit(taskDto);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }

    @PreAuthorize("hasAuthority('EDIT_TASKS')")
    @PutMapping("/changeStatus")
    public HttpEntity<?> changeStatus(@RequestBody TaskChangeStepDto taskChangeStepDto){

        ResponseMessage getAll = taskService.changeStatus(taskChangeStepDto);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }



//    @PreAuthorize("hasAnyAuthority('DELETE_EVERY_THINGS')")
//    @DeleteMapping("/admin/delete/{id}")
//    public HttpEntity<?> deleteByid(@PathVariable UUID id){
//
//        ResponseMessage getAll = taskService.deleteById(id);
//
//        return ResponseEntity.status(getAll.getStatus()).body(getAll);
//
//    }


    /**
     *
     * Manager Systeam
     *
     */
//    @PreAuthorize("hasAnyAuthority('EDIT_TASKS')")
//    @PutMapping("/sendtmanager")
//    public HttpEntity<?> changeStatusManager(@RequestBody TaskChangeStepDto taskChangeStepDto){
//
//        ResponseMessage getAll = taskService.changeStatus(taskChangeStepDto);
//
//        return ResponseEntity.status(getAll.getStatus()).body(getAll);
//
//    }




    /**
     *
     * Manager Systeam
     *
     */

//    @PreAuthorize("hasAuthority('GET_TASKS')")
//    @GetMapping("/manager/tomanager")
//    public HttpEntity<?> getAllSendToManagerForManager(@RequestParam int pageNumber){
//
//        ResponseMessage getAll = taskService.getAllForSuperAdmin(pageNumber, Status.SEND_TO_MANAGER);
//
//        return ResponseEntity.status(getAll.getStatus()).body(getAll);
//
//    }



    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/manager/inprogres")
    public HttpEntity<?> getAllinprogressForManager(@RequestParam int pageNumber){

        ResponseMessage getAll = taskService.getAllForSuperAdmin(pageNumber, Status.IN_PROGRESS);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }

    @PreAuthorize("hasAuthority('GET_TASKS')")
    @GetMapping("/manager/managerfinished")
    public HttpEntity<?> getManagerFinishedForManager(@RequestParam int pageNumber){

        ResponseMessage getAll = taskService.getAllForSuperAdmin(pageNumber, Status.FINISHED_USER);

        return ResponseEntity.status(getAll.getStatus()).body(getAll);

    }

}
