package zpl.valve.plugin;

import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.index.IndexSpec;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;
import zpl.valve.plugin.extension.Advantage;
import zpl.valve.plugin.extension.ApplicationArea;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.extension.Consultation;
import zpl.valve.plugin.extension.HomeAbout;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.extension.ProductCategory;
import zpl.valve.plugin.extension.Timeline;

import java.io.Console;

import static run.halo.app.extension.index.IndexAttributeFactory.multiValueAttribute;
import static run.halo.app.extension.index.IndexAttributeFactory.simpleAttribute;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class ValvePlugin extends BasePlugin {

    private final SchemeManager schemeManager;

    public ValvePlugin(PluginContext pluginContext, SchemeManager schemeManager,
        SchemeManager schemeManager1) {
        super(pluginContext);
        this.schemeManager = schemeManager1;
    }

    @Override
    public void start() {
        // 轮播图
        schemeManager.register(Banner.class, indexSpecs -> {
            // 轮播图索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(Banner.class,
                    banner -> String.valueOf(banner.getSpec().getPriority())))
            );
        });
        // 时间线
        schemeManager.register(Timeline.class, indexSpecs -> {
            // 时间线索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.datetime")
                .setIndexFunc(
                    simpleAttribute(Timeline.class, timeline -> timeline.getSpec().getDatetime()))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(Timeline.class,
                    timeline -> String.valueOf(timeline.getSpec().getPriority())))
            );
        });
        // 首页关于
        schemeManager.register(HomeAbout.class);
        // 应用领域
        schemeManager.register(ApplicationArea.class, indexSpecs -> {
            // 应用领域索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(ApplicationArea.class,
                    applicationArea -> String.valueOf(applicationArea.getSpec().getPriority())))
            );
        });
        // 核心优势
        schemeManager.register(Advantage.class, indexSpecs -> {
            // 核心优势索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(Advantage.class,
                    advantage -> String.valueOf(advantage.getSpec().getPriority())))
            );
        });
        // 产品分类
        schemeManager.register(ProductCategory.class, indexSpecs -> {
            //  产品分类索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(ProductCategory.class,
                    productCategory -> String.valueOf(productCategory.getSpec().getPriority())))
            );
        });
        // 产品
        schemeManager.register(Product.class, indexSpecs -> {
            //  产品索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.priority")
                .setIndexFunc(simpleAttribute(Product.class,
                    product -> String.valueOf(product.getSpec().getPriority())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.name")
                .setIndexFunc(simpleAttribute(Product.class,
                    product -> String.valueOf(product.getSpec().getName())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.description")
                .setIndexFunc(simpleAttribute(Product.class,
                    product -> String.valueOf(product.getSpec().getDescription())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.categoryMetadataName")
                .setIndexFunc(simpleAttribute(Product.class,
                    product -> String.valueOf(product.getSpec().getCategoryMetadataName())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.price")
                .setIndexFunc(simpleAttribute(Product.class,
                    product -> String.valueOf(product.getSpec().getPrice())))
            );
        });
        // 产品咨询
        schemeManager.register(Consultation.class, indexSpecs -> {
            //  产品咨询索引，排序需要指定
            indexSpecs.add(new IndexSpec()
                .setName("spec.name")
                .setIndexFunc(simpleAttribute(Consultation.class,
                    consultation -> String.valueOf(consultation.getSpec().getName())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.email")
                .setIndexFunc(simpleAttribute(Consultation.class,
                    consultation -> String.valueOf(consultation.getSpec().getEmail())))
            );
            indexSpecs.add(new IndexSpec()
                .setName("spec.content")
                .setIndexFunc(simpleAttribute(Consultation.class,
                    consultation -> String.valueOf(consultation.getSpec().getContent())))
            );
        });
        System.out.println("插件启动成功！");

    }

    @Override
    public void stop() {
        Scheme bannerScheme = schemeManager.get(Banner.class);
        schemeManager.unregister(bannerScheme);
        Scheme timelineScheme = schemeManager.get(Timeline.class);
        schemeManager.unregister(timelineScheme);
        Scheme homeAboutScheme = schemeManager.get(HomeAbout.class);
        schemeManager.unregister(homeAboutScheme);
        Scheme applicationAreaSchema = schemeManager.get(ApplicationArea.class);
        schemeManager.unregister(applicationAreaSchema);
        Scheme advantageSchema = schemeManager.get(Advantage.class);
        schemeManager.unregister(advantageSchema);
        Scheme productCategorySchema = schemeManager.get(ProductCategory.class);
        schemeManager.unregister(productCategorySchema);
        Scheme productSchema = schemeManager.get(Product.class);
        schemeManager.unregister(productSchema);
        Scheme consultationSchema = schemeManager.get(Consultation.class);
        schemeManager.unregister(consultationSchema);
        System.out.println("插件停止！");
    }
}
