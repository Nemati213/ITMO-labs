package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.utils.DragonDTO;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;

public class UpdateServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public UpdateServerCommand(CollectionManager collectionManager) {
        super("update", "обновить значение элемента коллекции по ID");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        try {
            if (request.getArgs().length != 1) {
                return new Response("Команда ожидает 1 аргумент!", false);
            }

            long id = Long.parseLong(request.getArgs()[0]);

            if (!collectionManager.isExist(id)) {
                return new Response("Дракон с ID " + id + " не найден.", false);
            }

            DragonDTO dto = request.getDragon();
            if (dto == null) return new Response("Объект для обновления не передан!", false);

            collectionManager.updateByID(id, dto);

            return new Response("Дракон с ID " + id + " успешно обновлен!", true);

        } catch (NumberFormatException e) {
            return new Response("ID должен быть числом!", false);
        } catch (IllegalArgumentException e) {
            return new Response("Ошибка валидации данных: " + e.getMessage(), false);
        }
    }
}