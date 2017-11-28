/*
 * Copyright 2016 John Grosh (jagrosh).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jselfbot.entities;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import jselfbot.utils.FormatUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author John Grosh (jagrosh)
 */
public class Emojis {
    private final HashMap<String, String> emojis;
    private final static String FILENAME = "emojis.json";
    private final static Logger LOG = LoggerFactory.getLogger("Emojis");
    
    public Emojis()
    {
        emojis = new HashMap<>();
        JSONObject obj;
        try {
            obj = new JSONObject(FormatUtil.join(Files.readAllLines(Paths.get(FILENAME)), "\n"));
            obj.keySet().stream().forEach(name -> emojis.put(name.toLowerCase(), obj.getString(name)));
            LOG.info("Successfully loaded "+emojis.keySet().size()+" custom emojis!");
        } catch(IOException e) {
            LOG.warn(FILENAME+" was not found! This can be ignored if you haven't set any custom emojis.");
        } catch(JSONException e) {
            LOG.error("The emojis file, "+FILENAME+" is corrupted. Please fix the file before adding emojis or the file will be overwritten.");
        }
    }
    
    private void write()
    {
        JSONObject obj = new JSONObject();
        emojis.keySet().stream().forEach(name -> obj.put(name, emojis.get(name)));
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILENAME), "UTF-8"))) {
            out.write(obj.toString(2));
        }catch(IOException e) {
            LOG.error("Failed to save emojis to "+FILENAME);
        }
    }
    
    public void setEmoji(String name, String content)
    {
        emojis.put(name.toLowerCase(), content);
        write();
    }
    
    public boolean deleteEmoji(String name)
    {
        boolean removed = emojis.remove(name.toLowerCase())!=null;
        if(removed)
            write();
        return removed;
    }
    
    public String getEmoji(String name)
    {
        return emojis.get(name.toLowerCase());
    }
    
    public Collection<String> getEmojiList()
    {
        return emojis.keySet();
    }
}
