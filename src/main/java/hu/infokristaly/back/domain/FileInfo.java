package hu.infokristaly.back.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Cacheable(value = true)
@Table(name = "file_info")
public class FileInfo  implements Serializable {
    private static final long serialVersionUID = -1021160915376137624L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Integer id;

    @Basic
    @Column(name = "unique_file_name")
    private String uniqueFileName;

    @Basic
    @Column(name = "lenght")
    private Long lenght;

    @Basic
    @ManyToOne
    @JoinColumn(name = "doc_info_id")
    private DocInfo docInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUniqueFileName() {
        return uniqueFileName;
    }

    public void setUniqueFileName(String uniqueFileName) {
        this.uniqueFileName = uniqueFileName;
    }

    public Long getLenght() {
        return lenght;
    }

    public void setLenght(Long lenght) {
        this.lenght = lenght;
    }

    public DocInfo getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(DocInfo docInfo) {
        this.docInfo = docInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileInfo)) return false;
        return Objects.equals(id, ((FileInfo)o).id) && Objects.equals(uniqueFileName, ((FileInfo)o).uniqueFileName) && Objects.equals(lenght, ((FileInfo)o).lenght) && Objects.equals(docInfo, ((FileInfo)o).docInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uniqueFileName, lenght, docInfo);
    }

}
