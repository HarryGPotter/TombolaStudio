/*
 * Copyright (c) 2018 Harry G potter (harry.g.potter@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package harrygpotter.tombola.tombolalib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Object of this calla represents an HTML template used to print set of cards.
 * 
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @since 1.8
 */
public class THtmlTemplate {

    public static enum THtmlTemplateType {
        A4_ONECARD_L,
        A4_TWOCARDS_P,
        A4_THREECARDS_P,
        A4_FOURCARDS_L,
        A4_SIXCARDS_P,
    }

    private String name;
    private THtmlTemplateType type;
    private String sourceFilePath;
    private String version;
    private int cardsPerBlock = -1;

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public THtmlTemplateType getType() {
        return type;
    }

    void setType(THtmlTemplateType type) {
        this.type = type;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getVersion() {
        return version;
    }

    void setVersion(String version) {
        this.version = version;
    }

    public int getCardsPerBlock() {
        return cardsPerBlock;
    }

    void setCardsPerBlock(int cardPerBlock) {
        this.cardsPerBlock = cardPerBlock;
    }

    /**
     * TODO(2.0) !!!Incomplete method!!! Search for templates in a file system directory
     * 
     * @param searchPaths the paths to search in.
     * @return the list of THtmlTemplate present within the search path
     */
    public static List<THtmlTemplate> searchTemplates(String[] searchPaths) {
        List<THtmlTemplate> result = new ArrayList<>();
        for (String path : searchPaths) {
            if (path != null) {
                File f = new File(path);
                if (f.isDirectory()) {
                    String[] elems = f.list();
                    for (String elem : elems) {
                        THtmlTemplate t = new THtmlTemplate();
                        t.setName(elem);
                        result.add(t);
                    }
                } else if (f.isFile()) {
                }
            }
        }
        return result;
    }

    // TODO(2.0) private static THtmlTemplateType analizeHtmlTemplate(String templateName)
    
}           // End Of File - Rel.(1.1)
