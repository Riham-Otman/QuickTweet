import Card from "react-bootstrap/Card";
import CardBody from "react-bootstrap/CardBody";
import CardTitle from "react-bootstrap/CardTitle";
import Image from "react-bootstrap/Image";
import { CardSubtitle } from "react-bootstrap";
import { CSSProperties } from "react";
import { Post as PostInterface } from "../types";

/**
 * Renders a single post within the application, displaying the post's content along with the author's information.
 *
 * This component utilizes the `Card` component from React Bootstrap to present the post in a styled card format,
 * enhancing the visual presentation. The card includes the author's avatar, name, and the post's title and content.
 *
 * The layout is designed with CSSProperties to ensure a consistent and responsive design across different devices.
 * The styles are defined for the card, header, image, and text elements to align with the application's design language.
 *
 * @component
 * @param {Object} props - The properties passed to the Post component.
 * @param {PostInterface} props.post - The post data to be displayed, conforming to the PostInterface structure.
 */
const Post = ({ post }: { post: PostInterface }) => {
  const cardStyle: CSSProperties = {
    background: "#F5F5F5",
    width: "80%",
    padding: "1%",
    margin: "auto",
    marginTop: "1%",
  };

  const headerStyle: CSSProperties = {
    display: "flex",
    flexDirection: "row",
  };

  const imageStyle: CSSProperties = {
    background: "white",
    borderRadius: "50%",
    width: 40,
    height: 40,
    display: "block",
  };

  const textStyle: CSSProperties = {
    display: "flex",
    flexDirection: "column",
    paddingLeft: "2%",
  };

  const titleStyle: CSSProperties = {
    fontSize: "medium",
  };

  const subtitleStyle: CSSProperties = {
    color: "GrayText",
    fontSize: "small",
  };

  return (
    <Card className='text-dark' style={cardStyle}>
      <CardBody className='text-dark' style={headerStyle}>
        <Image
          className='border'
          src={post.user.photo ? post.user.photo : import.meta.env.VITE_LOGO_PATH}
          style={imageStyle}
        />
        <div style={textStyle}>
          <CardTitle style={titleStyle}>{post.user.email}</CardTitle>
          <CardSubtitle style={subtitleStyle}>@{post.user.username}</CardSubtitle>
        </div>
      </CardBody>
      <CardBody>{post.content}</CardBody>
    </Card>
  );
};

export default Post;
