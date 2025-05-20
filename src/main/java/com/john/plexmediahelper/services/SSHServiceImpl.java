package com.john.plexmediahelper.services;

import com.jcraft.jsch.*;
import com.john.plexmediahelper.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class SSHServiceImpl implements SSHService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SSHServiceImpl.class);

    @Value("${ssh.host}")
    String host;
    @Value("${ssh.port}")
    int port;
    @Value("${ssh.username}")
    String username;
    @Value("${ssh.password}")
    String password;

    private JSch jsch = null;
    private Session session = null;

    public SSHServiceImpl() {
        jsch = new JSch();
    }

    @Override
    public void executeRemoteCommands(ArrayList<String> commandList) {
        try {
            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("Connecting to " + username + "@" + host + "...");
            session.connect();
            LOGGER.info("Executing commands...");
            multiExecuteRemoteCommands(session,commandList);
            LOGGER.info("Disconnect from remote host...");
            session.disconnect();
        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public ArrayList<String> getContentOfDir(String dir) {
        ArrayList<String> output = new ArrayList<>();
        BufferedReader reader = null;
        try {
            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("Connecting to " + username + "@" + host + "...");
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String command = "ls -1 \"" + dir + "\"";
            LOGGER.info("Executing command " + command);
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.matches(".*@eaDir.*"))
                    output.add(line);
            }
            channel.disconnect();
        } catch (IOException | JSchException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            LOGGER.info("Disconnect from remote host...");
            session.disconnect();
        }

        return output;
    }

    @Override
    public ArrayList<Item> getContentOfDirWithType(String dir) {
        ArrayList<Item> output = new ArrayList<>();
        BufferedReader reader = null;
        try {
            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("Connecting to " + username + "@" + host + "...");
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            //String command = "ls -1 " + dir;
            //searching for directories
            String command = "find " + dir +" -maxdepth 1 -type d";
            LOGGER.info("Executing command " + command);
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.equals(dir))
                    output.add(new Item(line, "dir", "", ""));
            }
            channel.disconnect();
            //searching for files
            channel = (ChannelExec) session.openChannel("exec");
            command = "find " + dir +" -maxdepth 1 -type f";
            LOGGER.info("Executing command " + command);
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            while ((line = reader.readLine()) != null) {
                    output.add(new Item(line, "file", "", ""));
            }

            channel.disconnect();
        } catch (IOException | JSchException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            LOGGER.info("Disconnect from remote host...");
            session.disconnect();
        }

        return output;
    }

    @Override
    public void deleteLinks(String dir) {
        LOGGER.info("Deleting links from " + dir + "...");
        //String command = "rm * " + dir;
        String command = "find " + dir + " -type l -delete";
        try {
            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("Connecting to " + username + "@" + host + "...");
            session.connect();
            int execStatus = executeRemoteCommand(session,command);
            if(execStatus == 0) {
                LOGGER.info("Links deleted successfully.");
            } else {
                LOGGER.error("Links delete command not executed successfully, execStatus = " + execStatus);
            }
        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public void deleteInvalidLinks(String dir) {
        ArrayList<String> output = new ArrayList<>();
        output = getInvalidLinks(dir);
        if(output.size() > 0) {
            LOGGER.info("Found " + output.size() + " orphaned symbolic links in " + dir +". Creating delete commands...");
            ArrayList<String> deleteLinksCommands = new ArrayList<>();
            String command = "";
            for(String link : output) {
                command = "rm " + link;
                deleteLinksCommands.add(command);
            }
            this.executeRemoteCommands(deleteLinksCommands);
        } else {
            LOGGER.info("No orphaned symbolic links found in " + dir);
        }
    }

    @Override
    public ArrayList<String> getInvalidLinks(String dir) {
        ArrayList<String> output = new ArrayList<>();
        BufferedReader reader = null;
        try {
            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            LOGGER.info("Connecting to " + username + "@" + host + "...");
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            String command = "find " + dir + " -xtype l";
            LOGGER.info("Executing command " + command);
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.matches(".*@eaDir.*"))
                    output.add(line);
            }
            channel.disconnect();
        } catch (IOException | JSchException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                    e.printStackTrace();
                }
            }
            LOGGER.info("Disconnect from remote host...");
            session.disconnect();
        }
        return output;
    }

    @Override
    public List<String> getContentOfFolderFromRemoteHost(String folder) {
        //String host = "192.168.1.120";
        //String username = "root";
        //String password = "lalaoapa"; // Or use private key authentication

        int port = 22; // Default SSH port

        try {
            JSch jsch = new JSch();

            // If you have a private key, load it here
            // jsch.addIdentity("/path/to/private_key");

            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            // Disable strict host key checking (optional)
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            // You are now connected to the remote server, and you can execute commands
            executeRemoteCommand(session, "ls -la " + folder);

            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }



    private static int multiExecuteRemoteCommands(Session session, ArrayList<String> commandList) throws JSchException {
        int exitStatus;
        int successful = 0;
        for(String command : commandList) {
            exitStatus = executeRemoteCommand2(session, command);
            if(exitStatus == 0) {
                successful++;
            }
        }
        if(successful == commandList.size()) {
            LOGGER.info("All commands executed successfully.");
            return 0;
        } else {
            LOGGER.error("Only " + successful + " out of " + commandList.size() + " commands executed successfully.");
            return 1;
        }
    }

    private static int executeRemoteCommand2(Session session, String command) throws JSchException {
        LOGGER.info("Executing command: " + command);
        int exitStatus = -1;
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        BufferedReader input = null;
        BufferedReader error = null;
        try {
            input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            error = new BufferedReader(new InputStreamReader(channel.getErrStream()));
            channel.connect();
            String line;
            //LOGGER.info("Command output:");
            boolean inputNotEmpty = true;
            while((line = input.readLine()) != null) {
                if(inputNotEmpty) {
                    LOGGER.info("Command output:");
                    inputNotEmpty = false;
                }
                LOGGER.info(line);
            }
            boolean errorNotEmpty = true;

            while ((line = error.readLine()) != null) {
                if(errorNotEmpty) {
                    LOGGER.error("Error stream output:");
                    errorNotEmpty = false;
                }
                LOGGER.error(line);
            }
            //wait for the command to finish
            while (!channel.isClosed()) {
                Thread.sleep(100);
            }
            exitStatus = channel.getExitStatus();
            LOGGER.info("Command exitStatus=" + exitStatus);
        } catch (IOException | InterruptedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if(input != null)
                    input.close();
                if(error != null)
                    error.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            channel.disconnect();
        }

        return exitStatus;
    }

    private static int executeRemoteCommand(Session session, String command) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        LOGGER.info("Executing command: " + command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);
        int exitStatus = -1;

        try (InputStream in = channel.getInputStream()) {
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    //System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    //System.out.println("exit-status: " + channel.getExitStatus());
                    exitStatus = channel.getExitStatus();
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            if(exitStatus != 0 ) {
                LOGGER.error("Command " + command + " not executed successfully, exiStatus=" + exitStatus);


            } else {
                LOGGER.info("Command executed successfully, exitStatus=" + exitStatus);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            channel.disconnect();
        }
        LOGGER.info("Exit status is: " + exitStatus);
        return exitStatus;
    }
}
