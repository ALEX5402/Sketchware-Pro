package mod.tyron.compiler;

import android.util.Log;

import com.besome.sketch.SketchApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import a.a.a.Jp;
import a.a.a.yq;
import mod.SketchwareUtil;
import mod.agus.jcoderz.dex.Dex;
import mod.agus.jcoderz.dx.merge.CollisionPolicy;
import mod.agus.jcoderz.dx.merge.DexMerger;
import mod.agus.jcoderz.editor.manage.library.locallibrary.ManageLocalLibrary;
import mod.tyron.compiler.Compiler;

import static com.besome.sketch.SketchApplication.getContext;

public class IncrementalDexMerger extends Compiler {

    public static final String TAG = IncrementalDexMerger.class.getSimpleName();

    public final String DEX_PATH;

    private final yq projectConfig;
    private final ManageLocalLibrary manageLocalLibrary;

    private final ArrayList<String> dexesGenerated = new ArrayList<>();
    private final ArrayList<String> builtInLibraries;
    private int currentDexNo = 0;

    public IncrementalDexMerger(yq projectConfig, ArrayList<String> builtInLibraries) {
        this.projectConfig = projectConfig;
        this.builtInLibraries = builtInLibraries;

        this.manageLocalLibrary = new ManageLocalLibrary(projectConfig.b);
        DEX_PATH = projectConfig.c + "/incremental/build";
    }

    @Override
    public ArrayList<Dex> getSourceFiles() {
        ArrayList<Dex> sources = new ArrayList<>();
        try {
            sources.addAll(getGeneratedDexFiles(new File(DEX_PATH)));

            String filesDir = getContext().getFilesDir().getAbsolutePath();

            for (String builtInLibrary : builtInLibraries) {
                builtInLibrary = builtInLibrary.substring(builtInLibrary.lastIndexOf("/"));
                builtInLibrary = SketchApplication.getContext().getFilesDir().getAbsolutePath() + "/libs/dexs/" + builtInLibrary + ".dex";
                Log.d(TAG, "Adding built in library: " + builtInLibrary);
                sources.add(new Dex(new File(builtInLibrary)));
            }

           for (String string : manageLocalLibrary.getDexLocalLibrary()) {
               sources.add(new Dex(new File(string)));
           }

           for (String string : manageLocalLibrary.getExtraDexes()) {
               sources.add(new Dex(new File(string)));
           }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sources;
    }

    @Override
    public void compile() {

        Log.d(TAG, "Task started");

        ArrayList<Dex> dexes = getSourceFiles();
        ArrayList<Dex> dexObjects = new ArrayList<>();
        int methodsMergedFile = 0;
        String outputPath = projectConfig.t + "/classes.dex";

        try {
            for (Dex dex : dexes) {
                int currentDexMethods = dex.methodIds().size();
                if (currentDexMethods + methodsMergedFile >= 65536) {
                    Log.d(TAG, "Creating dex file with index: " + currentDexNo);

                    mergeDexes(outputPath.replace("classes2.dex", "classes" + (currentDexNo > 0 ? currentDexNo : "" ) + ".dex"), dexObjects);
                    dexObjects.clear();
                    dexObjects.add(dex);
                    methodsMergedFile = currentDexMethods;
                    currentDexNo++;
                } else {
                    dexObjects.add(dex);
                    methodsMergedFile += currentDexMethods;
                }
            }
            if (dexObjects.size() > 0) {
                File out = new File(outputPath.replace("classes2.dex", "classes" + (currentDexNo > 0 ? currentDexNo : "" ) + ".dex"));
                if (out.exists()) {
                    out.delete();
                }
                out.createNewFile();

                mergeDexes(out.getAbsolutePath(), dexObjects);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Task merge: Done");
    }

    private void mergeDexes(String target, ArrayList<Dex> dexes) throws Exception {
        new DexMerger(dexes.toArray(new Dex[0]), CollisionPolicy.KEEP_FIRST).merge().writeTo(new File(target));
        //dexesGenerated.add(target);
    }

    private ArrayList<Dex> getGeneratedDexFiles(File input) throws IOException {

        ArrayList<Dex> foundDexes = new ArrayList<>();

        if(input.isDirectory()) {
            File[] files = input.listFiles();

            if(files != null) {
                for (File file : files) {
                    foundDexes.addAll(getGeneratedDexFiles(file));
                }
            }
        } else {
            if (input.getName().endsWith(".dex")) {
                foundDexes.add(new Dex(input));
            }
        }

        return foundDexes;
    }


}
