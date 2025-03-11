package com.jyami.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileChannelRepository {
    private final String fileName = "channel.ser";
    private final Map<UUID, Channel> channelMap;

    public FileChannelRepository() {
        this.channelMap = loadChannelList();
    }

    public void addChannel(Channel channel) {
        channelMap.put(UUID.randomUUID(), channel);
        saveChannelList();
    }

    public List<Channel> getChannelList() {
        return List.copyOf(channelMap.values());
    }

    public void saveChannelList() {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(channelMap);
        } catch (IOException e) {
            throw new RuntimeException("데이터를 저장하는데 실패했습니다.", e);
        }
    }

    public Map<UUID, Channel> loadChannelList() {
        try (FileInputStream fis = new FileInputStream(fileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object channelMap = ois.readObject();
            return (Map<UUID, Channel>) channelMap;
        } catch (FileNotFoundException e) {
            return new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("데이터를 불러오는데 실패했습니다", e);
        }
    }

}
