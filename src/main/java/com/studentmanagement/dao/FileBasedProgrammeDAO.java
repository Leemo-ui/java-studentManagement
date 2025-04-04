package com.studentmanagement.dao;

import com.studentmanagement.model.Programme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBasedProgrammeDAO implements ProgrammeDAO {
    private static final String FILE_PATH = "data/programmes.json";
    private final Gson gson;
    private List<Programme> programmes;

    public FileBasedProgrammeDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.programmes = new ArrayList<>();
        loadProgrammes();
    }

    private void loadProgrammes() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                TypeToken<List<Programme>> token = new TypeToken<List<Programme>>() {};
                programmes = gson.fromJson(reader, token.getType());
            } catch (IOException e) {
                e.printStackTrace();
                programmes = new ArrayList<>();
            }
        }
    }

    private void saveProgrammes() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(programmes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Programme save(Programme programme) throws Exception {
        if (programmes.stream().anyMatch(p -> p.getCode().equals(programme.getCode()))) {
            throw new Exception("Programme with code " + programme.getCode() + " already exists");
        }
        programmes.add(programme);
        saveProgrammes();
        return programme;
    }

    @Override
    public Programme update(Programme programme) throws Exception {
        for (int i = 0; i < programmes.size(); i++) {
            if (programmes.get(i).getCode().equals(programme.getCode())) {
                programmes.set(i, programme);
                saveProgrammes();
                return programme;
            }
        }
        throw new Exception("Programme not found");
    }

    @Override
    public void delete(String code) throws Exception {
        programmes = programmes.stream()
                .filter(programme -> !programme.getCode().equals(code))
                .collect(Collectors.toList());
        saveProgrammes();
    }

    @Override
    public Programme findById(String code) throws Exception {
        return programmes.stream()
                .filter(programme -> programme.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Programme> findAll() throws Exception {
        return new ArrayList<>(programmes);
    }
} 