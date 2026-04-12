export type AppIconType = "remix" | "lucide";

export type AppIcon = {
  type: AppIconType;
  name: string;
};

export type AppNavigationSectionItem = {
  type: "section";
  label: string;
};

export type AppNavigationLinkItem = {
  type?: "link";
  label: string;
  route: string;
  icon: AppIcon;
  exact?: boolean;
};

export type AppNavigationItem =
  | AppNavigationSectionItem
  | AppNavigationLinkItem;
