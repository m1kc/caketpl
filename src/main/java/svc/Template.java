package svc;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Source is mandatory")
    private String source;

    // standard constructors / setters / getters / toString

    public long getId() {
        return id;
    }
    public String getSource() {
        return source;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setSource(String source) {
        this.source = source;
    }
}