package ui.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {

    @GetMapping(value = ["","/"])
    fun index(): String = "Sucesso!!!"

}