package com.sample.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.Session;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.nativefs.NativeFileSystemView;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;

public class MainServer {

	public static void main(String[] args) throws Exception {
		SshServer sshd = SshServer.setUpDefaultServer();

		System.out.println("Starting Embedded SFTP server...");

		// sshd.setFileSystemFactory(new NativeFileSystemFactory());
		sshd.setFileSystemFactory(new FileSystemFactory() {

			public NativeFileSystemView createFileSystemView(
					final Session session) {
				return new NativeFileSystemView(session.getUsername(), false) {

					public String getVirtualUserDir() {						
						return null;
					}
				};
			};
		});

		sshd.setPort(22);

		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
				"hostkey.ser"));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {

			public boolean authenticate(String username, String password,
					ServerSession session) {
				// TODO Auto-generated method stub
				if (username.equals("user") && password.equals("password")) {
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

	}
}
