import type {Metadata} from "@halo-dev/api-client";

export interface BannerSpec {
  'name': string;
  'title': string;
  'subtitle': string;
  'bannerImagePc': string;
  'bannerImageMobile': string;
  'priority': number;
}

export interface BannerStatus {

}

export interface Banner {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': BannerSpec;
  'status'?: BannerStatus;
}

export interface BannerList {
  page: number;
  size: number;
  total: number;
  items: Array<Banner>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

export interface TimelineSpec {
  'datetime': string;
  'title': string;
  'priority': number;
}

export interface TimelineStatus {

}

export interface Timeline {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': TimelineSpec;
  'status'?: TimelineStatus;
}

export interface TimelineList {
  page: number;
  size: number;
  total: number;
  items: Array<Timeline>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

export interface HomeAboutSpec {
  'name': string;
  'description': string;
  'aboutUrl': string;
  'link1Text': string;
  'link1Url': string;
  'link1Image': string;
  'link2Text': string;
  'link2Url': string;
  'link2Image': string;
  'publicityImage': string;
  'publicityImageText1': string;
  'publicityImageText2': string;
  'publicityImageText3': string;
  'publicityImageText4': string;
  
}

export interface HomeAboutStatus {

}

export interface HomeAbout {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': HomeAboutSpec;
  'status'?: HomeAboutStatus;
}

export interface HomeAboutList {
  page: number;
  size: number;
  total: number;
  items: Array<HomeAbout>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

// 应用领域
export interface ApplicationAreaSpec {
  'name': string;
  'description': string;
  'image': string;
  'priority': number;
}

export interface ApplicationAreaStatus {

}

export interface ApplicationArea {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': ApplicationAreaSpec;
  'status'?: ApplicationAreaStatus;
}

export interface ApplicationAreaList {
  page: number;
  size: number;
  total: number;
  items: Array<ApplicationArea>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

// 核心优势
export interface AdvantageSpec {
  'title': string;
  'description': string;
  'moreLink': string;
  'image': string;
  'priority': number;
}

export interface AdvantageStatus {

}

export interface Advantage {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': AdvantageSpec;
  'status'?: AdvantageStatus;
}

export interface AdvantageList {
  page: number;
  size: number;
  total: number;
  items: Array<Advantage>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

// 产品分类
export interface ProductCategorySpec {
  'name': string;
  'description': string;
  'image': string;
  'priority': number;
}

export interface ProductCategoryStatus {
  // Add status fields if needed
}

export interface ProductCategory {
  'apiVersion': string;
  'kind': string;
  'metadata': Metadata;
  'spec': ProductCategorySpec;
  'status'?: ProductCategoryStatus;
}

export interface ProductCategoryList {
  page: number;
  size: number;
  total: number;
  items: Array<ProductCategory>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

// 产品
export interface ProductSpec {
  name: string;
  categoryMetadataName: string;
  description: string;
  standards: Standard[];
  images: ProductImage[];
  priority: number;
  sku?: string;
  price?: number;
}

export interface Standard {
  name: string;
  value: string;
}

export interface ProductImage {
  url: string;
  priority: number;
}

export interface ProductStatus {
  lastUpdateTime?: string;
}

export interface Product {
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  spec: ProductSpec;
  status?: ProductStatus;
}

export interface ProductList {
  page: number;
  size: number;
  total: number;
  items: Array<Product>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

export interface ConsultationSpec {
  name: string;
  email: string;
  content: string;
  status: 'Unread' | 'Read' | 'Responded';
}

export interface ConsultationStatus {
  // Add status fields if needed, for example:
  // lastUpdated: string;
}

export interface Consultation {
  apiVersion: string;
  kind: string;
  metadata: Metadata;
  spec: ConsultationSpec;
  status?: ConsultationStatus;
}

export interface ConsultationList {
  page: number;
  size: number;
  total: number;
  items: Array<Consultation>;
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}
