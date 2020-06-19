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
import java.util.StringTokenizer;

/**
 *
 * @author harry.g.potter@gmail.com
 */
public interface ICommand {
    
    /**
     * Past constructor method to be used to make available to the command object
     * the TombolaCards environment variables and parameters that could be needed
     * to accomplish its task.
     * 
     * @param env user-known environment variables and parameters key-value map
     * @param internals internal-only environment variables and parameters key-value map
     */
    void setEnvironment(Map<String, Object> env, Map<String, Object> internals);
    
    /**
     * Main method. Each command object must implement it to accomplish the specific
     * TombolaCards interactive task it has been created for.
     * 
     * @param st the string tokenizer object containing the remaining part of the
     * interactive command typed by the user, so that eventually additional parameter
     * can be parse and utilized.
     * @return an integer code indicating the exit status of the command.
     */
    int execute(StringTokenizer st);
    
    /**
     * Return a user-readable message after the command execution.
     * 
     * @return a user-readable message after the command execution.
     */
    String getResultMessage();
    
    /**
     * Helper method to display on video 'returing' messages to the user using a
     * standard formatting (for instance, pre-pending a response prompt to the message, etc.)
     * 
     * @param message the message you want to print
     */
    void echo(String message);
    
}           // End Of File - Rel.(1.1)
