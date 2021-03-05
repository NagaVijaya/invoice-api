package com.galvanize.orion.invoicify.entities;

import com.galvanize.orion.invoicify.utilities.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineItem> lineItems;
    private String company;
    private BigDecimal totalCost;
    private StatusEnum status;
    private String author;
    @CreatedDate
    private Date createdDate;
    private boolean archived;
    private Date modifiedDate;
    private BigDecimal discountPercent;

    public Invoice(){
        status = StatusEnum.UNPAID;
    }
}
