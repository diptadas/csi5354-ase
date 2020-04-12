package edu.baylor.cs.reflection;

import edu.baylor.cs.reflection.model.ClassStat;
import edu.baylor.cs.reflection.model.Component;
import edu.baylor.cs.reflection.model.Details;
import edu.baylor.cs.reflection.model.Summery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import java.util.List;

@Service
@Slf4j
public class StatService {

    public Summery getSummery() {
        List<Class> classes = TMSClassLoader.findAllClasses();

        Summery summery = new Summery();
        summery.setTotal(classes.size());

        for (Class cls : classes) {
            if (isController(cls)) {
                summery.setControllers(summery.getControllers() + 1);
            } else if (isEntity(cls)) {
                summery.setEntities(summery.getEntities() + 1);
            } else if (isRepository(cls)) {
                summery.setRepositories(summery.getRepositories() + 1);
            } else if (isService(cls)) {
                summery.setServices(summery.getServices() + 1);
            } else {
                summery.setOthers(summery.getOthers() + 1);
            }
        }

        return summery;
    }

    public Details getDetails() {
        List<Class> classes = TMSClassLoader.findAllClasses();

        Component controllers = new Component();
        Component entities = new Component();
        Component repositories = new Component();
        Component services = new Component();
        Component others = new Component();

        for (Class cls : classes) {
            ClassStat classStat = getClassStat(cls);

            if (isController(cls)) {
                controllers.getItems().add(classStat);
            } else if (isEntity(cls)) {
                entities.getItems().add(classStat);
            } else if (isRepository(cls)) {
                repositories.getItems().add(classStat);
            } else if (isService(cls)) {
                services.getItems().add(classStat);
            } else {
                others.getItems().add(classStat);
            }
        }

        controllers.setCount(controllers.getItems().size());
        entities.setCount(entities.getItems().size());
        repositories.setCount(repositories.getItems().size());
        services.setCount(services.getItems().size());
        others.setCount(others.getItems().size());

        return new Details(controllers, entities, repositories, services, others);
    }

    private ClassStat getClassStat(Class cls) {
        ClassStat classStat = new ClassStat();

        classStat.setName(cls.getSimpleName());
        classStat.setPackageName(cls.getPackage().getName());

        try {
            classStat.setFields(cls.getDeclaredFields().length);
        } catch (Exception | Error e) {
            log.error(e.toString());
        }

        try {
            classStat.setMethods(cls.getDeclaredMethods().length);
        } catch (Exception | Error e) {
            log.error(e.toString());
        }

        return classStat;
    }


    private boolean isController(Class cls) {
        try {
            return cls.getDeclaredAnnotation(Controller.class) != null || cls.getDeclaredAnnotation(RestController.class) != null;
        } catch (Exception | Error e) {
            log.error(e.toString());
        }
        return false;
    }

    private boolean isEntity(Class cls) {
        try {
            return cls.getDeclaredAnnotation(Entity.class) != null;
        } catch (Exception | Error e) {
            log.error(e.toString());
        }
        return false;
    }

    private boolean isRepository(Class cls) {
        try {
            return cls.getDeclaredAnnotation(Repository.class) != null;
        } catch (Exception | Error e) {
            log.error(e.toString());
        }
        return false;
    }

    private boolean isService(Class cls) {
        try {
            return cls.getDeclaredAnnotation(Service.class) != null;
        } catch (Exception | Error e) {
            log.error(e.toString());
        }
        return false;
    }
}
