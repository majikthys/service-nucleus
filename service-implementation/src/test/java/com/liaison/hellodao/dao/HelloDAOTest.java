package com.liaison.hellodao.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.RandomStringUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import org.testng.annotations.*;

import com.liaison.commons.jpa.DAOUtil;
import com.liaison.commons.jpa.OperationDelegate;
import com.liaison.commons.util.InitInitialContext;
import com.liaison.hellodao.model.HelloMoon;
import com.liaison.hellodao.model.HelloWorld;;


public class HelloDAOTest {
	static HelloDAO _dao = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InitInitialContext.init();
		_dao = new HelloDAO();
		initHelloWorldDaos();
	}

	@AfterClass
	public void tearDownAfterClass() throws Exception {
		deleteTestData();
	}
	
	

	/**
	 * Checks for Saturn's moons
	 * @throws Exception
	 */
	@Test
	public void testFindWorldsAndMoonsHappyPath() throws Exception {
		OperationDelegate findSaturnWorlds = new OperationDelegate() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> List<T> perform(EntityManager em)  {
				List<T> list = (List<T>) _dao.findHelloWorld(em,
						normalizeSGUID(Planet.SATURN.siSGUID));
				return ((List<T>) list);
			}
		};

		List<Object[]> list = DAOUtil.<Object[]> fetch(findSaturnWorlds);

		List<HelloWorld> helloWorlds = new ArrayList<HelloWorld>();
		List<HelloMoon> helloMoons = new ArrayList<HelloMoon>();
		List<String> helloMoonNames = new ArrayList<String>();
		
		for (Object[] oar : list) {
			for (Object obj : oar) {
				if (obj instanceof HelloWorld) {
					helloWorlds.add( (HelloWorld) obj);
				} else if (obj instanceof HelloMoon) {
					helloMoons.add((HelloMoon) obj);
					helloMoonNames.add(((HelloMoon) obj).getName());
				} else {
					fail("Only expecting moons and worlds");
				}
			}
		}

		assertThat(helloWorlds.size(), greaterThan(0));
		assertThat(helloWorlds.get(0).getName(), equalTo(Planet.SATURN.toString()));
		assertThat(helloMoons,	hasSize(Planet.SATURN.moons.size()));	
		assertThat(helloMoonNames.toArray(new String[helloMoonNames.size()]),	arrayContainingInAnyOrder(Planet.SATURN.getMoons().toArray(new String[helloMoonNames.size()])));


	}
	
	
	/**
	 * Checks for EARTH and JUPITER Moons
	 * @throws Exception
	 */
	@Test
	public void testFindOtherWorldsAndMoonsHappyPath() throws Exception {
		OperationDelegate op = new OperationDelegate() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> List<T> perform(EntityManager em)  {
				List<T> list = (List<T>) _dao.findHelloWorld(em,
						normalizeSGUID(Planet.EARTH.siSGUID)); //We know EARTH AND JUPITER HAVE SAME SGUID
				return ((List<T>) list);
			}
		};

		List<Object[]> list = DAOUtil.<Object[]> fetch(op);

		List<HelloWorld> helloWorlds = new ArrayList<HelloWorld>();
		List<HelloMoon> helloMoons = new ArrayList<HelloMoon>();
		List<String> helloMoonNames = new ArrayList<String>();
		Set<String> helloWorldNames = new HashSet<String>();
		
		for (Object[] oar : list) {
			for (Object obj : oar) {
				if (obj instanceof HelloWorld) {
					helloWorlds.add( (HelloWorld) obj);
					helloWorldNames.add(((HelloWorld) obj).getName());
				} else if (obj instanceof HelloMoon) {
					helloMoons.add((HelloMoon) obj);
					helloMoonNames.add(((HelloMoon) obj).getName());
				} else {
					fail("Only expecting moons and worlds");
				}
			}
		}

		List<String> moonNames = new ArrayList<String>();
		moonNames.addAll(Planet.JUPITER.getMoons());
		moonNames.addAll(Planet.EARTH.getMoons());
		
		assertThat(helloWorlds.size(), greaterThan(0));
		assertThat(helloWorldNames.toArray(new String[helloWorldNames.size()]),	arrayContainingInAnyOrder(new String[] {Planet.EARTH.toString(), Planet.JUPITER.toString()}));
		assertThat(helloMoons,	hasSize(Planet.JUPITER.moons.size()+Planet.EARTH.getMoons().size()));			
		assertThat(helloMoonNames.toArray(new String[helloMoonNames.size()]),	arrayContainingInAnyOrder(moonNames.toArray(new String[moonNames.size()])));
	}
	
	
	@Test
	public void testFindWorldsAgain() throws Exception {
		testFindWorldsAndMoonsHappyPath();
	}

	@Test
	public void fakeTest() {
		assertTrue(true);
	}
	
	/**
	 * pads SGUID with spaces to fill to 32 characters
	 * @param strSGUID
	 * @return
	 */
	public static String normalizeSGUID(String strSGUID) {
		int iLength = 32 - strSGUID.length();
		for (int i = 0; i < iLength; i++) {
			strSGUID += " ";
		}
		return (strSGUID);
	}
	
	public static final String TEST_WORLD_SIID = RandomStringUtils.randomAlphanumeric(4);

	/**
	 * Test data array
	 * @author jeremyfranklin-ross
	 */
	private enum Planet {
		SATURN(TEST_WORLD_SIID + "A", "Mimas", "Tethys", "Dione", "Rhea", "Titan"), 
		JUPITER(TEST_WORLD_SIID + "B", "Metis", "Adrastea","Amalthea", "Thebe", "Io"), 
		EARTH(TEST_WORLD_SIID + "B", "TheMoon");
		final String siSGUID;
		List<String> moons = new ArrayList<String>();

		Planet(String siSGUID, String... moonNames) {
			this.siSGUID = siSGUID;
			moons.addAll(Arrays.asList(moonNames));
		}

		List<String> getMoons() {
			return moons;
		}

		String getSiSGUID() {
			return siSGUID;
		}
	}

	/** 
	 * DAOUtil.persist to save newly created HelloWorlds with their 
	 * associated HelloMoons to DB.
	 * 
	 * @throws Exception
	 */
	public static void initHelloWorldDaos() throws Exception {
		System.out.println("Creating data...");

		for (Planet planet : Planet.values()) {
			HelloWorld helloWorld = new HelloWorld();
			helloWorld.setName(planet.toString());
			helloWorld.setSiSguid(planet.getSiSGUID());
			helloWorld.setHelloMoons(new ArrayList<HelloMoon>()); 
			for (String moonName : planet.getMoons()) {
				HelloMoon helloMoon = new HelloMoon();
				helloMoon.setName(moonName);
				helloWorld.getHelloMoons().add(helloMoon);
			}
			
			//Important bit right here:
			DAOUtil.persist(helloWorld);
		}
	}
	
	/**
	 * Delete all HelloWorlds and associated moons
	 * @throws Exception
	 */
	public void deleteTestData() throws Exception {
		System.out.println("Deleting data...");

		OperationDelegate op = new OperationDelegate() {
			@SuppressWarnings("unchecked")
			@Override
			public <T> List<T> perform(EntityManager em) {
				Query q = em.createQuery("SELECT FROM HelloWorld hw");
				List<T> worlds = q.getResultList();
				for (T world : worlds) {
					HelloWorld helloWorld = (HelloWorld) world;

					for (HelloMoon helloMoon : helloWorld.getHelloMoons()) {
						em.remove(helloMoon);
					}

					em.remove(helloWorld);
				}

				return (null);
			}
		};

		DAOUtil.perform(op);
	}
	
}
