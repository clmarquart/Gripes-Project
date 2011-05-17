package net.sf.gripes

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import groovy.lang.GroovyShell

import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.runtime.InvokerHelper

class GripesTemplateEngine extends SimpleTemplateEngine {
	private boolean verbose
	private static int counter = 1

	private GroovyShell groovyShell


    public GripesTemplateEngine() {
        this(GroovyShell.class.getClassLoader());
    }

    public GripesTemplateEngine(boolean verbose) {
        this(GroovyShell.class.getClassLoader());
        setVerbose(verbose);
    }

    public GripesTemplateEngine(ClassLoader parentLoader) {
        this(new GroovyShell(parentLoader));
    }

    public GripesTemplateEngine(GroovyShell groovyShell) {
        this.groovyShell = groovyShell;
    }

	
	public Template createTemplate(Reader text) throws CompilationFailedException, IOException {
        GripesTemplate template = new GripesTemplate();
		
		def strtext = template.parseDefaults(text.getText())
		Reader reader = new BufferedReader(new StringReader(strtext))

        String script = template.parse(reader);
        if (verbose) {
            println("\n-- script source --")
            print(script)
            println("\n-- script end --\n")
        }
        try {
            template.script = groovyShell.parse(script, "GripesTemplateScript" + counter++ + ".groovy");
        } catch (Exception e) {
            throw new GroovyRuntimeException("Failed to parse template script (your template may contain an error or be trying to use expressions not currently supported): " + e.getMessage());
        }
        return template;
    }


    private static class GripesTemplate implements Template {
        protected Script script;

        public Writable make() {
            return make(null);
        }

        public Writable make(final Map map) {
            return new Writable() {
                /**
                 * Write the template document with the set binding applied to the writer.
                 *
                 * @see groovy.lang.Writable#writeTo(java.io.Writer)
                 */
                public Writer writeTo(Writer writer) {
                    Binding binding;
                    if (map == null)
                        binding = new Binding()
                    else
                        binding = new Binding(map)

                    Script scriptObject = InvokerHelper.createScript(script.getClass(), binding)
                    PrintWriter pw = new PrintWriter(writer)

					scriptObject.setProperty("gripes",new GripesTagLib(binding: binding, request: binding.request))
                    scriptObject.setProperty("out", pw)
                    scriptObject.run()
                    pw.flush()

                    return writer
                }

                /**
                 * Convert the template and binding into a result String.
                 *
                 * @see java.lang.Object#toString()
                 */
                public String toString() {
                    StringWriter sw = new StringWriter()
                    writeTo(sw)
                    return sw.toString()
                }
            };
        }

		protected String parseDefaults(String text) {		
			def regex = /\$\{([^\?]*)\?\:([^\}]*)\}/
			text = text.replaceAll(/(?m)\$\{[^\}]*\}/, { 
			    def found = (it =~ regex)
			    if (found.find()){
			        '${try{'+found[0][1]+"}catch(e){"+found[0][2]+"}}"
			    } else {
			     it
			    }
			})
			text
		}

        /**
         * Parse the text document looking for &lt;% or &lt;%= and then call 
		 * out to the appropriate handler, otherwise copy the text directly
         * into the script while escaping quotes.
         *
         * @param reader a reader for the template text
         * @return the parsed text
         * @throws IOException if something goes wrong
         */
        protected String parse(Reader reader) {
            if (!reader.markSupported()) {
                reader = new BufferedReader(reader)
            }
            StringWriter sw = new StringWriter()
            startScript(sw)

            int c;
            while ((c = reader.read()) != -1) {
                if (c == '<') {
                    reader.mark(1);
                    c = reader.read();
                    if (c != '%') {
                        sw.write('<');
                        reader.reset();
                    } else {
                        reader.mark(1);
                        c = reader.read();
                        if (c == '=') {
                            groovyExpression(reader, sw);
                        } else {
                            reader.reset();
                            groovySection(reader, sw);
                        }
                    }
                    continue; // at least '<' is consumed ... read next chars.
                }
                if (c == '$') {
                    reader.mark(1);
                    c = reader.read();
                    if (c != '{') {
                        sw.write('$');
                        reader.reset();
                    } else {
                        reader.mark(1);
                        sw.write('${');
                        processGSstring(reader, sw);
                    }
                    continue; // at least '$' is consumed ... read next chars.
                }
                if (c == '\"') {
                    sw.write('\\');
                }
                /*
                 * Handle raw new line characters.
                 */
                if (c == '\n' || c == '\r') {
                    if (c == '\r') { // on Windows, "\r\n" is a new line.
                        reader.mark(1);
                        c = reader.read();
                        if (c != '\n') {
                            reader.reset();
                        }
                    }
                    sw.write("\n");
                    continue;
                }
                sw.write(c);
            }
            endScript(sw);
            return sw.toString();
        }

        private void startScript(StringWriter sw) {
            sw.write("/* Generated by GripesTemplateEngine */\n");
            sw.write("out.print(\"\"\"");
        }

        private void endScript(StringWriter sw) {
            sw.write("\"\"\");\n");
        }

        private void processGSstring(Reader reader, StringWriter sw) throws IOException {
            int c;
            while ((c = reader.read()) != -1) {
                if (c != '\n' && c != '\r')
                    sw.write(c)

                if (c == '}')
                    break
            }
        }

        /**
         * Closes the currently open write and writes out the following 
		 * text as a GString expression until it reaches an end %&gt;.
         *
         * @param reader a reader for the template text
         * @param sw     a StringWriter to write expression content
		 *
         * @throws IOException if something goes wrong
         */
        private void groovyExpression(Reader reader, StringWriter sw) throws IOException {
            sw.write('${');
            int c;
            while ((c = reader.read()) != -1) {
                if (c == '%') {
                    c = reader.read();
                    if (c != '>') {
                        sw.write('%');
                    } else {
                        break;
                    }
                }
                if (c != '\n' && c != '\r') {
                    sw.write(c);
                }
            }
            sw.write("}");
        }

        /**
         * Closes the currently open write and writes the following text as normal Groovy script code until it reaches an end %>.
         *
         * @param reader a reader for the template text
         * @param sw     a StringWriter to write expression content
         * @throws IOException if something goes wrong
         */
        private void groovySection(Reader reader, StringWriter sw) throws IOException {
            sw.write("\"\"\");");
            int c;
            while ((c = reader.read()) != -1) {
                if (c == '%') {
                    c = reader.read();
                    if (c != '>') {
                        sw.write('%');
                    } else {
                        break;
                    }
                }
                /* Don't eat EOL chars in sections - as they are valid instruction separators.
                 * See http://jira.codehaus.org/browse/GROOVY-980
                 */
                // if (c != '\n' && c != '\r') {
                sw.write(c);
                //}
            }
            sw.write(";\nout.print(\"\"\"");
        }
    }
}