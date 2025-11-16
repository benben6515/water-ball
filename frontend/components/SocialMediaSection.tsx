import React from 'react';
import socialLinksData from '@/data/social-links.json';
import { FaLine, FaFacebook, FaInstagram, FaYoutube, FaLink } from 'react-icons/fa';

interface SocialLink {
  id: string;
  name: string;
  icon: string;
  url: string;
  color: string;
}

const getIcon = (iconName: string) => {
  const icons: { [key: string]: React.ReactElement } = {
    line: <FaLine className="text-xl" />,
    facebook: <FaFacebook className="text-xl" />,
    instagram: <FaInstagram className="text-xl" />,
    youtube: <FaYoutube className="text-xl" />,
    link: <FaLink className="text-xl" />,
  };
  return icons[iconName] || <FaLink className="text-xl" />;
};

export default function SocialMediaSection() {
  const socialLinks: SocialLink[] = socialLinksData;

  return (
    <section className="py-16 px-6">
      <div className="container mx-auto max-w-4xl">
        <h2 className="text-2xl font-bold mb-8">社群媒體</h2>

        <div className="flex flex-wrap gap-4">
          {socialLinks.map((link) => (
            <a
              key={link.id}
              href={link.url}
              target="_blank"
              rel="noopener noreferrer"
              className="flex items-center gap-2 px-4 py-2 border border-amber-200 rounded-lg hover:border-green-500 hover:text-green-600 hover:bg-green-50/30 transition-all"
            >
              {getIcon(link.icon)}
              <span className="text-sm">{link.name}</span>
            </a>
          ))}
        </div>
      </div>
    </section>
  );
}
