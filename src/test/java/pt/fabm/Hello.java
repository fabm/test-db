package pt.fabm;

import javax.jws.WebService;

@WebService
public interface Hello {
    String sayHi(String name);
}
