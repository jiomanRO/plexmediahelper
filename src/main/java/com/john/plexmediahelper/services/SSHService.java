package com.john.plexmediahelper.services;

import com.john.plexmediahelper.model.Item;

import java.util.ArrayList;
import java.util.List;

public interface SSHService {
    public List<String> getContentOfFolderFromRemoteHost(String folder);
    public void executeRemoteCommands(ArrayList<String>commandList);
    public ArrayList<String> getContentOfDir(String dir);
    public ArrayList<Item> getContentOfDirWithType(String dir);
    public void deleteLinks(String dir);
    public void deleteInvalidLinks(String dir);

    public ArrayList<String> getInvalidLinks(String dir);
}
