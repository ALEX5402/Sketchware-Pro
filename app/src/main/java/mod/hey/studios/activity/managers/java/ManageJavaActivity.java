package mod.hey.studios.activity.managers.java;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sketchware.remod.Resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import mod.SketchwareUtil;
import mod.agus.jcoderz.lib.FilePathUtil;
import mod.agus.jcoderz.lib.FileResConfig;
import mod.agus.jcoderz.lib.FileUtil;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.ConfigActivity;

public class ManageJavaActivity extends Activity {

    private static final String ACTIVITY_TEMPLATE = "package %s;\n\nimport android.app.Activity;\nimport android.os.Bundle;\n\npublic class %s extends Activity {\n     \n      @Override\n     protected void onCreate(Bundle savedInstanceState) {\n              super.onCreate(savedInstanceState);\n       }\n     \n}";
    private static final String CLASS_TEMPLATE = "package %s;\n\npublic class %s {\n        \n}";
    private final ArrayList<String> currentTree = new ArrayList<>();
    private String current_path;
    private FilePathUtil fpu;
    private FileResConfig frc;
    private GridView gridView;
    private MyAdapter myadp;
    private String sc_id;
    private TextView tv_nofiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Resources.layout.manage_file);

        sc_id = getIntent().getStringExtra("sc_id");
        Helper.fixFileprovider();
        setupUI();
        frc = new FileResConfig(sc_id);
        fpu = new FilePathUtil();
        current_path = Uri.parse(fpu.getPathJava(sc_id)).getPath();
        refresh();
    }

    private void setupUI() {
        gridView = findViewById(Resources.id.list_file);
        gridView.setNumColumns(1);

        FloatingActionButton fab = findViewById(Resources.id.fab_plus);
        fab.setOnClickListener(v -> showCreateDialog());

        tv_nofiles = findViewById(Resources.id.text_info);
        tv_nofiles.setText("No files");

        ((TextView) findViewById(Resources.id.tx_toolbar_title)).setText("Java Manager");
        ImageView back = findViewById(Resources.id.ig_toolbar_back);
        Helper.applyRippleToToolbarView(back);

        back.setOnClickListener(Helper.getBackPressedClickListener(this));

        ImageView ig_load_file = findViewById(Resources.id.ig_toolbar_load_file);
        ig_load_file.setVisibility(View.VISIBLE);
        Helper.applyRippleToToolbarView(ig_load_file);
        ig_load_file.setOnClickListener(v -> showLoadDialog());
    }

    @Override
    public void onBackPressed() {
        if (Objects.equals(
                Uri.parse(current_path).getPath(),
                Uri.parse(fpu.getPathJava(sc_id)).getPath()
        )) {
            super.onBackPressed();
            return;
        }

        current_path = current_path.substring(0, current_path.lastIndexOf("/"));
        refresh();
    }

    private String trimPath(String str) {
        return str.endsWith("/") ? str.substring(0, str.length() - 1) : str;
    }

    private String getCurrentPkgName() {
        String pkgName = getIntent().getStringExtra("pkgName");

        try {
            String trimmedPath = trimPath(fpu.getPathJava(sc_id));
            String substring = current_path.substring(current_path.indexOf(trimmedPath) + trimmedPath.length());

            if (substring.endsWith("/")) {
                substring = substring.substring(0, substring.length() - 1);
            }

            if (substring.startsWith("/")) {
                substring = substring.substring(1);
            }

            String replace = substring.replace("/", ".");
            return replace.isEmpty() ? pkgName : pkgName + "." + replace;
        } catch (Exception e) {
            return pkgName;
        }
    }

    private void showCreateDialog() {
        final AlertDialog create = new AlertDialog.Builder(this).create();
        View inflate = getLayoutInflater().inflate(Resources.layout.dialog_create_new_file_layout, null);

        final EditText editText = inflate.findViewById(Resources.id.dialog_edittext_name);
        inflate.findViewById(Resources.id.dialog_text_cancel).setOnClickListener(v -> create.dismiss());

        final RadioGroup radio_fileType = inflate.findViewById(Resources.id.dialog_radio_filetype);
        inflate.findViewById(Resources.id.dialog_text_save).setOnClickListener(v -> {
            String format;

            if (editText.getText().toString().isEmpty()) {
                SketchwareUtil.toastError("Invalid file name");
                return;
            }

            String name = editText.getText().toString();
            String currentPkgName = getCurrentPkgName();

            switch (radio_fileType.getCheckedRadioButtonId()) {
                case Resources.id.dialog_radio_filetype_class:
                    format = String.format(CLASS_TEMPLATE, currentPkgName, name);
                    break;

                case Resources.id.dialog_radio_filetype_activity:
                    format = String.format(ACTIVITY_TEMPLATE, currentPkgName, name);
                    break;

                case Resources.id.radio_button_folder:
                    FileUtil.makeDir(new File(current_path, name).getAbsolutePath());
                    refresh();
                    SketchwareUtil.toast("Folder was created successfully");
                    create.dismiss();
                    return;

                default:
                    SketchwareUtil.toast("Select a file type");
                    return;
            }

            FileUtil.writeFile(new File(current_path, name + ".java").getAbsolutePath(), format);
            refresh();
            SketchwareUtil.toast("File was created successfully");
            create.dismiss();
        });

        create.setOnDismissListener(dialog -> SketchwareUtil.hideKeyboard());
        create.setView(inflate);
        create.show();

        editText.requestFocus();
        SketchwareUtil.showKeyboard();
    }

    private void showLoadDialog() {
        DialogProperties properties = new DialogProperties();

        properties.selection_mode = 1;
        properties.selection_type = 0;
        properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        properties.error_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        properties.offset = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        properties.extensions = new String[]{"java"};

        FilePickerDialog pickerDialog = new FilePickerDialog(this, properties);

        pickerDialog.setTitle("Select a Java file");
        pickerDialog.setDialogSelectionListener(selections -> {
            for (String path : selections) {
                String fileName = Uri.parse(path).getLastPathSegment();
                String fileContent = FileUtil.readFile(path);

                if (fileContent.contains("package ")) {
                    String substring = fileContent.substring(fileContent.indexOf("package"), fileContent.indexOf(";"));
                    FileUtil.writeFile(new File(current_path, fileName).getAbsolutePath(), fileContent.replace(substring, "package " + getCurrentPkgName()));
                    refresh();
                } else {
                    SketchwareUtil.toastError("File " + fileName + " is not a valid Java file");
                }
            }
        });

        pickerDialog.show();
    }

    private void showRenameDialog(final int position) {
        final AlertDialog create = new AlertDialog.Builder(this).create();
        View inflate = getLayoutInflater().inflate(Resources.layout.dialog_input_layout, null);

        final EditText fileName = inflate.findViewById(Resources.id.edittext_change_name);
        fileName.setText(myadp.getFileName(position));

        inflate.findViewById(Resources.id.text_cancel).setOnClickListener(v -> create.dismiss());
        inflate.findViewById(Resources.id.text_save).setOnClickListener(view -> {
            if (!fileName.getText().toString().isEmpty()) {
                if (!myadp.isFolder(position) && frc.getJavaManifestList().contains(myadp.getFullName(position))) {
                    frc.getJavaManifestList().remove(myadp.getFullName(position));
                    FileUtil.writeFile(fpu.getManifestJava(sc_id), new Gson().toJson(frc.listJavaManifest));
                    SketchwareUtil.toast("NOTE: Removed activity from manifest");
                }

                FileUtil.renameFile(myadp.getItem(position), new File(current_path, fileName.getText().toString()).getAbsolutePath());
                refresh();
                SketchwareUtil.toast("Renamed successfully");
            }

            create.dismiss();
        });

        create.setView(inflate);
        create.setOnDismissListener(dialog -> SketchwareUtil.hideKeyboard());
        create.show();

        fileName.requestFocus();
        SketchwareUtil.showKeyboard();
    }

    private void showDeleteDialog(final int position) {
        final boolean isInManifest = frc.getJavaManifestList().contains(myadp.getFullName(position));

        new AlertDialog.Builder(this)
                .setTitle(myadp.getFileName(position))
                .setMessage("Are you sure you want to delete this " + (myadp.isFolder(position) ? "folder" : "file") + "?"
                        + (isInManifest ? " This will also remove it from AndroidManifest." : "")
                        + " This action cannot be reversed!")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (!myadp.isFolder(position) && isInManifest) {
                        frc.getJavaManifestList().remove(myadp.getFullName(position));
                        FileUtil.writeFile(fpu.getManifestJava(sc_id), new Gson().toJson(frc.listJavaManifest));
                    }

                    FileUtil.deleteFile(myadp.getItem(position));
                    refresh();
                    SketchwareUtil.toast("Deleted successfully");
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    private void refresh() {
        if (!FileUtil.isExistFile(fpu.getPathJava(sc_id))) {
            FileUtil.makeDir(fpu.getPathJava(sc_id));
            refresh();
        }

        if (!FileUtil.isExistFile(fpu.getManifestJava(sc_id))) {
            FileUtil.writeFile(fpu.getManifestJava(sc_id), "");
            refresh();
        }

        currentTree.clear();
        FileUtil.listDir(current_path, currentTree);
        sort(currentTree);

        myadp = new MyAdapter();

        gridView.setAdapter(myadp);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            if (myadp.isFolder(position)) {
                current_path = myadp.getItem(position);
                refresh();
                return;
            }
            myadp.goEditFile(position);
        });

        tv_nofiles.setVisibility(currentTree.size() == 0 ? View.VISIBLE : View.GONE);
    }

    // This sort function also appears on ManageAssetsActivity
    // better put this on a helper somewhere to reduce some bytes
    void sort(ArrayList<String> paths) {
        ArrayList<String> directories = new ArrayList<>();
        ArrayList<String> files = new ArrayList<>();

        for (String str : paths) {
            if (FileUtil.isDirectory(str)) {
                directories.add(str);
            } else {
                files.add(str);
            }
        }

        directories.sort(String.CASE_INSENSITIVE_ORDER);
        files.sort(String.CASE_INSENSITIVE_ORDER);
        paths.clear();
        paths.addAll(directories);
        paths.addAll(files);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public String getItem(int position) {
            return currentTree.get(position);
        }

        @Override
        public int getCount() {
            return currentTree.size();
        }

        /**
         * Gets the full package name of the Java file.
         *
         * @param position The Java file's position in this adapter's {@code ArrayList}
         * @return The full package name of the Java file
         */
        public String getFullName(int position) {
            String readFile = FileUtil.readFile(getItem(position));

            if (!readFile.contains("package ") || !readFile.contains(";")) {
                return getFileNameWoExt(position);
            }

            return readFile.substring(readFile.indexOf("package ") + 8, readFile.indexOf(";")) + "." + getFileNameWoExt(position);
        }

        /**
         * Gets the Java file's file name with extension.
         *
         * @param position The Java file's position in this adapter's {@code ArrayList}
         * @return The file's file name with extension
         */
        public String getFileName(int position) {
            String item = getItem(position);
            return item.substring(item.lastIndexOf("/") + 1);
        }

        /**
         * Gets the Java file's file name without extension.
         *
         * @param position The Java file's position in this adapter's {@code ArrayList}
         * @return The file's file name without extension
         */
        public String getFileNameWoExt(int position) {
            String fileName = getFileName(position);

            if (fileName.contains(".")) {
                return fileName.substring(0, fileName.lastIndexOf("."));
            }

            return fileName;
        }

        public boolean isFolder(int position) {
            return FileUtil.isDirectory(getItem(position));
        }

        public void goEditFile(int position) {
            Intent intent = new Intent();

            if (ConfigActivity.isLegacyCeEnabled()) {
                intent.setClass(getApplicationContext(), mod.hey.studios.activity.SrcCodeEditor.class);
            } else {
                intent.setClass(getApplicationContext(), mod.hey.studios.code.SrcCodeEditor.class);
            }

            intent.putExtra("java", "");
            intent.putExtra("title", getFileName(position));
            intent.putExtra("content", getItem(position));

            startActivity(intent);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(Resources.layout.manage_java_item_hs, null);
            }

            TextView name = convertView.findViewById(Resources.id.title);
            ImageView icon = convertView.findViewById(Resources.id.icon);
            ImageView more = convertView.findViewById(Resources.id.more);

            name.setText(getFileName(position));
            icon.setImageResource(isFolder(position) ? Resources.drawable.ic_folder_48dp : Resources.drawable.java_96);

            Helper.applyRipple(ManageJavaActivity.this, more);

            more.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(ManageJavaActivity.this, v);
                popupMenu.inflate(2131492893);

                Menu popupMenuMenu = popupMenu.getMenu();
                popupMenuMenu.clear();

                boolean isActivityInManifest = frc.getJavaManifestList().contains(getFullName(position));
                boolean isServiceInManifest = frc.getServiceManifestList().contains(getFullName(position));

                if (!isFolder(position)) {
                    if (isActivityInManifest) {
                        popupMenuMenu.add("Remove Activity from manifest");
                    } else if (!isServiceInManifest) {
                        popupMenuMenu.add("Add as Activity to manifest");
                    }

                    if (isServiceInManifest) {
                        popupMenuMenu.add("Remove Service from manifest");
                    } else if (!isActivityInManifest) {
                        popupMenuMenu.add("Add as Service to manifest");
                    }

                    popupMenuMenu.add("Edit");
                    popupMenuMenu.add("Edit with...");
                }

                popupMenuMenu.add("Rename");
                popupMenuMenu.add("Delete");

                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getTitle().toString()) {
                        case "Add as Activity to manifest":
                            frc.getJavaManifestList().add(getFullName(position));
                            FileUtil.writeFile(fpu.getManifestJava(sc_id), new Gson().toJson(frc.listJavaManifest));
                            SketchwareUtil.toast("Successfully added " + getFileNameWoExt(position) + " as Activity to AndroidManifest");
                            break;

                        case "Remove Activity from manifest":
                            if (frc.getJavaManifestList().remove(getFullName(position))) {
                                FileUtil.writeFile(fpu.getManifestJava(sc_id), new Gson().toJson(frc.listJavaManifest));
                                SketchwareUtil.toast("Successfully removed Activity " + getFileNameWoExt(position) + " from AndroidManifest");
                            } else {
                                SketchwareUtil.toast("Activity was not defined in AndroidManifest.");
                            }
                            break;

                        case "Add as Service to manifest":
                            frc.getServiceManifestList().add(getFullName(position));
                            FileUtil.writeFile(fpu.getManifestService(sc_id), new Gson().toJson(frc.listServiceManifest));
                            SketchwareUtil.toast("Successfully added " + getFileNameWoExt(position) + " as Service to AndroidManifest");
                            break;

                        case "Remove Service from manifest":
                            if (frc.getServiceManifestList().remove(getFullName(position))) {
                                FileUtil.writeFile(fpu.getManifestService(sc_id), new Gson().toJson(frc.listServiceManifest));
                                SketchwareUtil.toast("Successfully removed Service " + getFileNameWoExt(position) + " from AndroidManifest");
                            } else {
                                SketchwareUtil.toast("Service was not defined in AndroidManifest.");
                            }
                            break;

                        case "Edit":
                            goEditFile(position);
                            break;

                        case "Edit with...":
                            Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                            launchIntent.setDataAndType(Uri.fromFile(new File(getItem(position))), "text/plain");
                            startActivity(launchIntent);
                            break;

                        case "Rename":
                            showRenameDialog(position);
                            break;

                        case "Delete":
                            showDeleteDialog(position);
                            break;

                        default:
                            return false;
                    }

                    return true;
                });

                popupMenu.show();
            });

            return convertView;
        }
    }
}