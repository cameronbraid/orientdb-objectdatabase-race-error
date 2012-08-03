package raceerror;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import raceerror.data.Foo;
import raceerror.data.Thing;

import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.orientechnologies.orient.object.db.OObjectDatabasePool;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;


public class Main {

	public static void main(String[] args) throws Exception {
		
		clean();
		
		OServer server = embed();
		
		int count = 10;
		final CountDownLatch latch = new CountDownLatch(count);
		final CountDownLatch sem = new CountDownLatch(1);
		for (int i = 0; i < count; i++) {
			final int num = i;
			new Thread() {
				public void run() {
					try {
						OObjectDatabaseTx db = newDb();
						Thing o = new Thing("thing-" + num);
//						Thing o = db.newInstance(Thing.class, "thing-" + num);
						o.getFoos().add(new Foo("foo-" + num));
						sem.await();
						o = db.save(o);
						db.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
					latch.countDown();
				};
			}.start();
		}
			
		sem.countDown();
		latch.await();

		OObjectDatabaseTx db = newDb();

		for (Thing o : db.browseClass(Thing.class)) {
			System.out.println(o.getName() + " : " + Joiner.on(",").join(o.getFoos()));
		}
		db.close();
		
		server.shutdown();
		
		clean();


	}


	
	public static OServer embed() throws Exception, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		final OServer server = OServerMain.create();
		server.startup(new File("src/main/resources/orient.xml"));
		server.activate();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown();
			}
		});
		return server;
	}

	public static void clean() throws IOException {
		File file = new File("data/raceerror");
		if (file.exists()) {
			Files.deleteRecursively(file);
		}
	}

	static OObjectDatabaseTx newDb() {
		try {
			OObjectDatabaseTx db = OObjectDatabasePool.global().acquire("remote:localhost:2424/raceerror", "admin", "admin");
			db.getEntityManager().registerEntityClasses("raceerror.data");
			return db;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
