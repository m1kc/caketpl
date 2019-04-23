package svc;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import svc.Template;

@Repository
public interface TemplateRepository extends CrudRepository<Template, Long> {}