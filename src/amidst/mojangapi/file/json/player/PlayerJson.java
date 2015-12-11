package amidst.mojangapi.file.json.player;

import java.util.Collections;
import java.util.List;

import amidst.documentation.GsonConstructor;
import amidst.documentation.Immutable;

@Immutable
public class PlayerJson {
	private volatile String id;
	private volatile String name;
	private volatile List<PropertyJson> properties = Collections.emptyList();

	@GsonConstructor
	public PlayerJson() {
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<PropertyJson> getProperties() {
		return properties;
	}

	public TexturesPropertyJson readTexturesProperty() {
		for (PropertyJson property : properties) {
			TexturesPropertyJson texturesProperty = property
					.tryDecodeTexturesProperty();
			if (texturesProperty != null) {
				return texturesProperty;
			}
		}
		return null;
	}

	public String getSkinUrl() {
		return readTexturesProperty().getTextures().getSKIN().getUrl();
	}
}
