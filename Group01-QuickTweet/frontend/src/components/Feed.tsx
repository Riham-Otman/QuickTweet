import { faFilter, faPencil } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useState } from "react";
import { Button, Col, Container, Row, Spinner } from "react-bootstrap";
import Pagination from "react-bootstrap/Pagination";
import { fetchPosts } from "../api/postApi";
import { FeedProps, Post as PostInterface } from "../types";
import Footer from "./Footer";
import Nav from "./Nav";
import Post from "./Post";
import PostForm from "./PostForm";
import Filters from "./Filters";
import FriendRecommendations from "./RecommendFriends";

/**
 * Feed component that displays posts and allows users to create new posts.
 * It fetches posts asynchronously when the component mounts or when the `user` prop changes.
 * It supports pagination, displaying a fixed number of posts per page and generating pagination controls dynamically.
 * A modal can be toggled to display or hide additional UI elements, such as a form for creating a new post.
 *
 * @component
 * @param {Object} props - The component props.
 * @param {Object} props.user - The current user object. Used to fetch posts related to the user.
 * @param {Function} props.setAuth - Function to update authentication state in the parent component.
 * @returns {React.ReactElement} The Feed component.
 */
const Feed = ({ user, setAuth }: FeedProps): React.ReactElement => {
  const [currentPage, setCurrentPage] = useState(1);
  const [modal, setModal] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const [posts, setPosts] = useState<PostInterface[]>([]);
  const [loading, setLoading] = useState(true);
  const totalPages = Math.ceil(posts.length / 5);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const data = await fetchPosts(user);
      setPosts(data);
      setLoading(false);
    };

    if (loading) {
      fetchData();
    }
  }, [user.username, loading]);

  const toggleDropdown = () => {
    setShowDropdown(prev => !prev);
  };

  // calculate starting and ending index of current page (assuming posts are inserted as an array)
  const startIndex = (currentPage - 1) * 5;
  const endIndex = Math.min(startIndex + 5, posts.length);

  // use these indices to determine which posts exist in currently visited page
  const currentPosts = posts.slice(startIndex, endIndex);

  // generate a new page 'button' for each new page
  const paginationItems = [];
  for (let i = 1; i <= totalPages; i++) {
    paginationItems.push(
      <Pagination.Item key={i} active={i === currentPage} onClick={() => setCurrentPage(i)}>
        {i}
      </Pagination.Item>
    );
  }

  const paginationStyle = {
    marginTop: "5%",
  };

  if (loading) {
    return <Spinner animation='border' variant='primary' />;
  } else {
    return (
      <div>
        <Nav
          username={user.username}
          setAuth={setAuth}
          isAdmin={user.role === "ADMIN"}
          user={user}
        />
        <Container fluid>
          {loading ? (
            <Spinner animation='border' variant='primary' />
          ) : (
            <>
              <Row className='my-4 justify-content-end'>
                <Col className='col-1'>
                  <Button onClick={() => setModal(true)}>
                    <FontAwesomeIcon icon={faPencil} />
                  </Button>
                </Col>
                <Col className='col-2'>
                  <Button onClick={toggleDropdown}>
                    <FontAwesomeIcon icon={faFilter} />
                  </Button>
                  {showDropdown && <Filters posts={posts} setPosts={setPosts}></Filters>}
                </Col>
              </Row>
              <PostForm modal={modal} setModal={setModal} user={user} setLoading={setLoading} />
              {currentPosts.map((post, i) => (
                <Post post={post} key={i} />
              ))}
              {posts.length > 0 && (
                <Pagination className='justify-content-center' style={paginationStyle}>
                  <Pagination.First onClick={() => setCurrentPage(1)} />
                  <Pagination.Prev
                    onClick={() =>
                      currentPage != 1 ? setCurrentPage(currentPage - 1) : setCurrentPage(1)
                    }
                  />
                  {paginationItems.slice(
                    currentPage > 3 ? currentPage - 3 : 0,
                    currentPage < totalPages - 3 ? currentPage + 2 : totalPages
                  )}
                  <Pagination.Next
                    onClick={() =>
                      currentPage != totalPages
                        ? setCurrentPage(currentPage + 1)
                        : setCurrentPage(totalPages)
                    }
                  />
                  <Pagination.Last onClick={() => setCurrentPage(totalPages)} />
                </Pagination>
              )}
            </>
          )}
          <FriendRecommendations currentUsername={user.username} />
        </Container>
        <Footer />
      </div>
    );
  }
};

export default Feed;
