package jp.co.axa.apidemo.entities;

import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  Employee Entity that is provided by Axa Java challenge.
 *  Assuming that this is the base entity that I will need to enhance API from there...
 */
@Entity
@Builder
@Table(name="EMPLOYEE")
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {

	private static final long serialVersionUID = -3598658114654598982L;

	@ApiModelProperty(value = "Employee's ID", required = false, position = 1, example = "10", hidden=true)
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

	@ApiModelProperty(value = "Employee's Name", required = true, position = 2, example = "Axa")
    @Getter
    @Setter
    @Column(name="name", nullable=false)
    private String name;

	@ApiModelProperty(value = "Employee's Salary", required = true, position = 3, example = "300")
    @Getter
    @Setter
    @Column(name="salary", nullable = false)
    private Integer salary;

	@ApiModelProperty(value = "Employee's Department", required = true, position = 4, example = "Technology")
    @Getter
    @Setter
    @Column(name="department", nullable = false)
    private String department;

	public static Employee of(String name, Integer salary, String department) {
		return Employee.builder().name(name).salary(salary).department(department).build();
	  }
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		return true;
	}    
}
