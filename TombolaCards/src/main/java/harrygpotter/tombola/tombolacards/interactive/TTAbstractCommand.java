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
package harrygpotter.tombola.tombolacards.interactive;

import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This is the super class for all command object used in TombolaCards to implement
 * each single command behaviors available within the interactive mode. Here are 
 * implemented common methods and grouped instance variables usually used in each
 * non abstract command class.
 * 
 * @see ICommand
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public abstract class TTAbstractCommand implements ICommand {
    
    protected String sCommandName = "";
    protected String sResult = "";
    protected Map<String, Object> envMap;
    protected Map<String, Object> internals;
    
    @Override
    public String getResultMessage() { return this. sResult; }
    
    @Override
    public void setEnvironment(Map<String, Object> envMap, Map<String, Object> internals) {
        this.envMap = envMap;
        this.internals = internals;
    }
    
    @Override
    public void echo(String msg) {
        String rPrompt = (String) internals.get("rPrompt");
        if (rPrompt != null) {
            System.out.print(rPrompt +" "+ msg);
        } else {
            System.out.print(msg);
        }
    }

    protected String[] parseParameter(StringTokenizer st) {
        String[] params = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            params[i++] = st.nextToken();
        }
        return params;
    }
    
    protected boolean askYesNoQuestion(String text, boolean defValue) {
        String defString = (defValue ? "Yes" : "No");
        echo(text + " [Yes | No] (default="+defString+"): ");
        String readLine = (new Scanner(System.in)).nextLine();
        if (readLine == null || readLine.length() == 0) {
            return defValue;
        }
        return readLine.equalsIgnoreCase("Y") || readLine.equalsIgnoreCase("Yes");
    }
}
