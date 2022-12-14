package firok.tool.tinwrench.main;

import firok.tool.alloywrench.bean.CocoData;
import firok.tool.alloywrench.util.Jsons;
import javafx.application.Platform;

import java.io.File;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static firok.topaz.Collections.mappingKeyValue;

class LoadCocoThread extends Thread
{
	final File fileLabel, folderImage;
	final TinWrenchController controller;
	public LoadCocoThread(File fileLabel, File folderImage, TinWrenchController controller)
	{
		this.fileLabel = fileLabel;
		this.folderImage = folderImage;
		this.controller = controller;
	}
	@Override
	public void run()
	{
		controller.isReactive = false;
		var om = Jsons.omDecimal();
		Map<Integer, TinWrenchController.RefImage> mapImage;
		Map<Integer, TinWrenchController.RefCate> mapCate;
		Map<Integer, TinWrenchController.RefAnno> mapAnno;
		try
		{
			System.out.println("加载数据集: " + fileLabel);
			var coco = om.readValue(fileLabel, CocoData.class);
			mapImage = mappingKeyValue(coco.getImages(), CocoData.Image::getId, TinWrenchController.RefImage::new);
			mapCate = mappingKeyValue(coco.getCategories(), CocoData.Category::getId, TinWrenchController.RefCate::new);
			mapAnno = mappingKeyValue(
					coco.getAnnotations(),
					CocoData.Annotation::getId,
					anno -> new TinWrenchController.RefAnno(
							anno,
							Optional.ofNullable(mapCate.get(anno.getCategoryId()))
									.map(TinWrenchController.RefCate::cate)
									.orElseThrow(() -> new IllegalArgumentException("找不到类型"))
					)
			);
		}
		catch (Exception any)
		{
			any.printStackTrace(System.err);
			controller.isReactive = true;
			return;
		}

		Platform.runLater(() -> {
			controller.mapImage = mapImage;
			controller.mapCate = mapCate;
			controller.mapAnno = mapAnno;
			controller.ivScale.setX(1);
			controller.ivScale.setY(1);

			var listImage = controller.fxListImage.getItems();
			listImage.clear();
			listImage.addAll(controller.mapImage.values());

			controller.fxListAnno.getItems().clear();
			controller.fxListCate.getItems().clear();
			var paneImageChildren = controller.paneImage.getChildren();
			for(var render : controller.listAnnoRender)
			{
				paneImageChildren.removeAll(render.listPath);
				paneImageChildren.remove(render.text);
			}
			controller.listAnnoRender.clear();

			controller.fileLabel = fileLabel;
			controller.folderImage = folderImage;
			Configs.opened(fileLabel, folderImage);

			controller.isReactive = true;
		});
	}
}
