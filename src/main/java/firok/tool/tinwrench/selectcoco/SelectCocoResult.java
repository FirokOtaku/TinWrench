package firok.tool.tinwrench.selectcoco;

import java.io.File;

public record SelectCocoResult(
		boolean isConfirm,
		File fileLabel,
		File folderImage
)
{
}
