    package com.game.listener;
    import com.game.model.GameConstant;
    import com.game.utils.MessageUtil;
    import com.google.gson.Gson;

    import javax.servlet.ServletContextEvent;
    import javax.servlet.ServletContextListener;
    import javax.servlet.annotation.WebListener;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.Reader;
    import java.nio.charset.StandardCharsets;

    @WebListener
    public class GameConstantLoader implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent servletContextEvent) {
            try (Reader reader = new InputStreamReader(
                    GameConstantLoader.class.getClassLoader().getResourceAsStream("game_constant.json"),
                    StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                GameConstant gameConstant = gson.fromJson(reader, GameConstant.class);

                GameConstant.setInstance(gameConstant);

            } catch (IOException | NullPointerException e) {
                throw new RuntimeException(MessageUtil.get("error.load.file"));
            }
        }
    }