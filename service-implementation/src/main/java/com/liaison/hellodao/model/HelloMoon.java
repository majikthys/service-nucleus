package com.liaison.hellodao.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the HELLO_MOON database table.
 * 
 */
@Entity
@Table(name="HELLO_MOON")
public class HelloMoon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="HM_PGUID")
	private String hmPguid;

	private String name;

	//bi-directional many-to-one association to HelloWorld
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="HW_PGUID")
	private HelloWorld helloWorld;

	public HelloMoon() {
	}

	public String getHmPguid() {
		return this.hmPguid;
	}

	public void setHmPguid(String hmPguid) {
		this.hmPguid = hmPguid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HelloWorld getHelloWorld() {
		return this.helloWorld;
	}

	public void setHelloWorld(HelloWorld helloWorld) {
		this.helloWorld = helloWorld;
	}

}