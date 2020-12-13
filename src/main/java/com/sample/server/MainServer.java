package com.sample.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;

public class MainServer {

	

	public static void main(String[] args) throws Exception {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = MainServer.class.getClassLoader().getResourceAsStream(
					"config.properties");

			// load a properties file
			prop.load(input);
			
			String userName = prop.getProperty("username");
			String pwd = prop.getProperty("password");
			int port = Integer.parseInt(prop.getProperty("port"));
			String directory = prop.getProperty("directory");

		

			SshServer sshd = SshServer.setUpDefaultServer();

			System.out.println("Starting Embedded SFTP server...");

			sshd.setFileSystemFactory(new VirtualFileSystemFactory(directory));

			sshd.setPort(port);

			sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
					"hostkey.ser"));
			sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

				public boolean authenticate(String username, String password,
						ServerSession session) {

					if (username.equals(userName) && password.equals(pwd)) {
						return true;
					}
					return false;
				}
			});

			CommandFactory myCommandFactory = new CommandFactory() {

				public Command createCommand(String command) {
					return null;
				}
			};
			sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));

			List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();

			namedFactoryList.add(new SftpSubsystem.Factory());
			sshd.setSubsystemFactories(namedFactoryList);

			sshd.start();
			System.out.println("Started Embedded SFTP server...");

		} catch (IOException ex) {			
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
