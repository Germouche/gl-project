package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

//import for parralel compilation
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl37
 * @date 01/01/2022
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println(37);
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            System.out.println("La commande decac ne peut-être appélé seule. Voici les options disponibles:");
            System.out.println("    -b permet d'afficher le nom de l'équipe et ne peut-être qu'appelé seule.");
            System.out.println("    -p permet d'uniquement faire  la construction de l'arbre et d'afficher un " +
                    "programme decac");
            System.out.println("    -v arrête decac après l’étape de vérifications" +
                    "  (ne produit aucune sortie en l’absence d’erreur)");
            System.out.println("    -n supprime les tests à l’exécution spécifiés dans" +
                    "  les points 11.1 et 11.3 de la sémantique de Deca.");
            System.out.println("    -r X limite les registres banalisés disponibles à" +
                    "  R0 ... R{X-1}, avec 4 <= X <= 16 (pas implémentée");
            System.out.println("    -d active les traces de debug. Répéter" +
                    "  l’option plusieurs fois pour avoir plus de" +
                    "  traces.");
            System.out.println("    -P s’il y a plusieurs fichiers sources," +
                    "  lance la compilation des fichiers en" +
                    "  parallèle (pour accélérer la compilation) (pas implémentée)");
            System.exit(0);
        }
        else if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            throw new UnsupportedOperationException("Parallel build not yet implemented");
        } else if (options.getParse()){
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile_p_option()) {
                    error = true;
                }
            }
        } else if (options.getVerification()) {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile_v_option()) {
                    error = true;
                }
            }
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
