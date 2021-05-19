package mod.hey.studios.editor.manage.block;

public class ExtraBlockInfo {
    public transient boolean isMissing;
    private String code = "";
    private int color = 0;
    private String name = "";
    private int paletteColor = 0;
    private String spec = "";
    private String spec2 = "";

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String str) {
        this.code = str;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public int getPaletteColor() {
        return this.paletteColor;
    }

    public void setPaletteColor(int i) {
        this.paletteColor = i;
    }

    public String getSpec() {
        return this.spec;
    }

    public void setSpec(String str) {
        this.spec = str;
    }

    public String getSpec2() {
        return this.spec2;
    }

    public void setSpec2(String str) {
        this.spec2 = str;
    }
}
