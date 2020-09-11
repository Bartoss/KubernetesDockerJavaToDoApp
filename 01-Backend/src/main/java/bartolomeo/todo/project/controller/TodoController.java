package bartolomeo.todo.project.controller;

import bartolomeo.todo.project.model.Todo;
import bartolomeo.todo.project.service.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class TodoController {

    @Autowired
    TodoRepository todoRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        // Date - dd/MM/yyyy
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/list-todos", method = RequestMethod.GET)
    public String showTodos(ModelMap modelMap){
        String name = getLoggedInUserName(modelMap);
        modelMap.put("todos", todoRepository.findByUser(name));
        return "list-todos";
    }

    private String getLoggedInUserName(ModelMap modelMap){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    @RequestMapping(value = "/add-todo", method = RequestMethod.GET)
    public String showAddTodoPage(ModelMap modelMap){
        modelMap.addAttribute("todo", new Todo(0, getLoggedInUserName(modelMap), "Default Desc", new Date(), false));

        return "todo";
    }

    @RequestMapping(value = "/delete-todo", method = RequestMethod.GET)
    public String deleteTodo(@RequestParam int id) {

        //if(id==1)
        //throw new RuntimeException("Something went wrong");
        todoRepository.deleteById(id);
        //service.deleteTodo(id);
        return "redirect:/list-todos";
    }

    public String showUpdateTodoPage(@RequestParam int id, ModelMap modelMap){
        Todo todo = todoRepository.findById(id).get();
        //Todo todo = service.retriveTodo(id);
        modelMap.put("todo", todo);
        return "todo";
    }

    @RequestMapping(value = "/update-todo", method = RequestMethod.POST)
    public String updateTodo(ModelMap modelMap, @Valid Todo todo, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "todo";
        }

        todo.setUser(getLoggedInUserName(modelMap));

        todoRepository.save(todo);

        return "redirect:/list-todos";
    }

    public String addTodo(ModelMap modelMap, @Valid Todo todo, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return "todo";
        }

        todo.setUser(getLoggedInUserName(modelMap));
        todoRepository.save(todo);

        return "redirect:/list-todos";
    }




}
