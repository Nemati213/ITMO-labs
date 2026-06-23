package ru.itmo.nemat.utils;

import ru.itmo.nemat.models.*;
import ru.itmo.nemat.utils.Validator;
import java.util.function.Supplier;

public class DragonFactory {

    private static DragonFactory instance;
    private DragonFactory() {

    }

    public static DragonFactory getInstance() {
        if (instance == null) {
            instance = new DragonFactory();
        }
        return instance;
    }

    public DragonDTO buildFromStrings(String nameStr, String ageStr, String xStr, String yStr,
                                      String colorStr, String typeStr, String charStr,
                                      String hasCaveStr, String depthStr, String treasuresStr) {

        String name = Validator.validateName(nameStr);
        long age = Validator.validateAge(ageStr);
        Coordinates coords = new Coordinates(Validator.validateX(xStr), Validator.validateY(yStr));
        Color color = Validator.validateColor(colorStr);
        DragonType type = Validator.validateDragonType(typeStr);
        DragonCharacter character = Validator.validateDragonCharacter(charStr);

        DragonCave cave = null;
        if (Validator.validateCaveDecision(hasCaveStr)) {
            cave = new DragonCave(Validator.validateDepth(depthStr), Validator.validateTreasures(treasuresStr));
        }

        return new DragonDTO(name, coords, age, color, type, character, cave);
    }

    public DragonDTO buildFromSequence(Supplier<String> lineReader) {

        String name = Validator.validateName(lineReader.get());


        long age = Validator.validateAge(lineReader.get());

        Coordinates coords = new Coordinates(
                Validator.validateX(lineReader.get()),
                Validator.validateY(lineReader.get())
        );

        Color color = Validator.validateColor(lineReader.get());
        DragonType type = Validator.validateDragonType(lineReader.get());
        DragonCharacter character = Validator.validateDragonCharacter(lineReader.get());

        DragonCave cave = null;

        if (Validator.validateCaveDecision(lineReader.get())) {
            cave = new DragonCave(
                    Validator.validateDepth(lineReader.get()),
                    Validator.validateTreasures(lineReader.get())
            );
        }

        return new DragonDTO(name, coords, age, color, type, character, cave);
    }
}