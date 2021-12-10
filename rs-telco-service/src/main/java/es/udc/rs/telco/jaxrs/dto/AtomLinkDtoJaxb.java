package es.udc.rs.telco.jaxrs.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

import java.net.URI;

@XmlType(name = "atomLinkType", namespace = "http://www.w3.org/2005/Atom")
public class AtomLinkDtoJaxb {
    @XmlAttribute(name = "href", required = true)
    private URI href;
    @XmlAttribute(name = "rel",  required = true)
    private String rel;
    @XmlAttribute(name = "type", required = true)
    private String type;
    @XmlAttribute(name = "title", required = true)
    private String title;

    public AtomLinkDtoJaxb() {
    }

    public AtomLinkDtoJaxb(URI href, String rel, String type, String title) {
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.title = title;
    }
    public URI getHref() {
        return href;
    }
    public void setHref(URI href) {
        this.href = href;
    }
    public String getRel() {
        return rel;
    }
    public void setRel(String rel) {
        this.rel = rel;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}
