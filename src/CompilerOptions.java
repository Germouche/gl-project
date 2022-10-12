package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl37
 * @date 01/01/2022
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getParse() {
        return parse;
    }

    public boolean getVerification() {
        return verification;
    }

    public boolean getNoCheck() {
        return no_check;
    }

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verification = false;
    private boolean no_check = false;
    private boolean registers = false;
    private int number_of_registers = -1;

    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {
        int nb_options = 0;

        for(String argument: args) {
            switch(argument) {
                case "-b" :
                    // l'option de peut-être qu'utilisé seul, sinon on lance une fatal error car on ne peut pas
                    // compiler
                    if (args.length != 1) {
                        throw new IllegalArgumentException("L’option ’-b’ ne peut être utilisée que sans autre option.");
                    }
                    printBanner = true;
                    break;

                case "-p" :
                    parse = true;
                    nb_options++;
                    break;

                case "-v" :
                    verification = true;
                    nb_options++;
                    break;

                case "-n" :
                    no_check = true;
                    nb_options++;
                    break;

                case "-r" :
                    registers = true;
                    nb_options++;
                    break;

                case "-d" :
                    debug++;
                    break;

                case "-P" :
                    parallel = true;
                    break;
            }
            // Si registers est l'argument d'avant, il est true, on récupère alors le nombre de registre
            if (registers) {
                try {
                    Integer.parseInt(argument);
                } catch (IllegalArgumentException e) {
                }
                number_of_registers = Integer.parseInt(argument);
                if ( number_of_registers < 4 || number_of_registers > 16) {
                    throw new IllegalArgumentException("il faut qu'il y ai entre 4 et 16 registres banalisés.");
                }
                // On repasse registrers en false pouur ne pas parser de nouveau les registres
                registers = false;
                nb_options++;
            }
        }
        // Les options -p et -v ne sont pas compatibles
        if (parse && verification) {
            throw new IllegalArgumentException("Les options -p et -v ne sont pas compatibles");
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        if (!no_check) {
            assert assertsEnabled = true; // Intentional side effect!!!
        }
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
        nb_options += getDebug();
        for (int cpt = nb_options; cpt < args.length; cpt++) {
            String file_name = args[cpt];
            File file = new File(file_name);
            this.sourceFiles.add(file); // ultra-basique, ne prend qu'un programme sans parametres, à completer plus tard
        }


        //throw new UnsupportedOperationException("not yet implemented");
    }

    protected void displayUsage() {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
