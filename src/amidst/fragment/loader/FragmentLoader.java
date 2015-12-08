package amidst.fragment.loader;

import amidst.fragment.Fragment;
import amidst.fragment.layer.LayerDeclaration;

public abstract class FragmentLoader {
	protected final LayerDeclaration declaration;

	public FragmentLoader(LayerDeclaration declaration) {
		this.declaration = declaration;
	}

	public LayerDeclaration getLayerDeclaration() {
		return declaration;
	}

	public abstract void load(Fragment fragment);

	public abstract void reload(Fragment fragment);
}