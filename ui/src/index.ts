import {definePlugin} from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import {IconImageAddLine, IconTimerLine, IconBookRead} from "@halo-dev/components";
import {h, markRaw} from "vue";
import BannerView from "@/views/BannerView.vue";
import TimelineView from "@/views/TimelineView.vue";
import HomeAboutView from "./views/HomeAboutView.vue";
import {Icon} from '@iconify/vue';
import ApplicationAreaView from "@/views/ApplicationAreaView.vue";
import AdvantageView from "./views/AdvantageView.vue";
import ProductCategoryView from "@/views/ProductCategoryView.vue";
import ProductView from "@/views/ProductView.vue";
import ConsultationView from "@/views/ConsultationView.vue";

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/banner",
        name: "Banner",
        component: BannerView,
        meta: {
          title: "轮播图",
          searchable: true,
          menu: {
            name: "轮播图",
            group: "首页定制",
            icon: markRaw(IconImageAddLine),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/timeline",
        name: "Timeline",
        component: TimelineView,
        meta: {
          title: "时间线",
          searchable: true,
          menu: {
            name: "时间线",
            group: "首页定制",
            icon: markRaw(IconTimerLine),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/homeAbout",
        name: "HomeAbout",
        component: HomeAboutView,
        meta: {
          title: "首页关于",
          searchable: true,
          menu: {
            name: "首页关于",
            group: "首页定制",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/applicationArea",
        name: "ApplicationArea",
        component: ApplicationAreaView,
        meta: {
          title: "应用领域",
          searchable: true,
          menu: {
            name: "应用领域",
            group: "首页定制",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/advantage",
        name: "Advantage",
        component: AdvantageView,
        meta: {
          title: "核心优势",
          searchable: true,
          menu: {
            name: "核心优势",
            group: "首页定制",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/productCategory",
        name: "ProductCategory",
        component: ProductCategoryView,
        meta: {
          title: "产品分类",
          searchable: true,
          menu: {
            name: "产品分类",
            group: "产品",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/product",
        name: "Product",
        component: ProductView,
        meta: {
          title: "产品详情",
          searchable: true,
          menu: {
            name: "产品详情",
            group: "产品",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
    {
      parentName: "Root",
      route: {
        path: "/consultation",
        name: "Consultation",
        component: ConsultationView,
        meta: {
          title: "产品咨询",
          searchable: true,
          menu: {
            name: "产品咨询",
            group: "产品",
            icon: markRaw(IconBookRead),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
