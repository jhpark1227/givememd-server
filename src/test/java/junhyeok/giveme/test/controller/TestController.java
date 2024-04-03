package junhyeok.giveme.test.controller;

import junhyeok.giveme.test.exception.TestApplicationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("test")
    String returnHello(){
        return "Hello";
    }

    @GetMapping("test/exception")
    void generateException(){
        throw new TestApplicationException();
    }
}
